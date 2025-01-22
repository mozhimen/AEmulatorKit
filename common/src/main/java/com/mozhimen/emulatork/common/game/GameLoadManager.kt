package com.mozhimen.emulatork.common.game

import android.content.Context
import android.os.Build
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.load.LoadException
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.basic.load.SLoadError
import com.mozhimen.emulatork.common.EmulatorKCommon
import com.mozhimen.emulatork.common.archive.ArchiveCoherencyEngine
import com.mozhimen.emulatork.common.bios.BiosManager
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.common.core.CorePropertyManager
import com.mozhimen.emulatork.common.save.SaveManager
import com.mozhimen.emulatork.common.save.SaveStateManager
import com.mozhimen.emulatork.common.system.SystemProvider
import com.mozhimen.emulatork.core.type.ECoreType
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.db.game.entities.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

/**
 * @ClassName GameLoader
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class GameLoadManager(
    private val emulatorKCommon: EmulatorKCommon,
    private val legacySaveStateManager: SaveStateManager,
    private val legacySaveManager: SaveManager,
    private val legacyCorePropertyManager: CorePropertyManager,
    private val retrogradeDatabase: RetrogradeDatabase,
    private val archiveCoherencyEngine: ArchiveCoherencyEngine,
    private val storageProvider: StorageDirProvider,
    private val biosManager: BiosManager
) : IUtilK {

    fun load(
        appContext: Context,
        game: Game,
        loadSave: Boolean,
        coreBundle: CoreBundle,
        directLoad: Boolean
    ): Flow<SGameLoadState> = flow {
        try {
            emit(SGameLoadState.LoadingCore)

            val systemBundle = SystemProvider.findSysByName(game.systemName)

            if (!isArchitectureSupported(coreBundle)) {
                throw LoadException(SLoadError.UnsupportedArchitecture)
            }

            val coreLibrary = runCatching {
                findLibrary(appContext, coreBundle.eCoreType)!!.absolutePath
            }.getOrElse {
                throw LoadException(SLoadError.LoadCore)
            }

            emit(SGameLoadState.LoadingGame)

            val missingBiosFiles = biosManager.getMissingBiosFiles(coreBundle, game)
            if (missingBiosFiles.isNotEmpty()) {
                throw LoadException(SLoadError.MissingBiosFiles(missingBiosFiles))
            }

            val gameFiles = runCatching {
                val useVFS = coreBundle.supportsLibretroVFS && directLoad
                val dataFiles = retrogradeDatabase.dataFileDao().selectDataFilesForGame(game.id)
                emulatorKCommon.getGameFiles(game, dataFiles, useVFS)
            }.getOrElse { throw it }

            val saveRAMData = runCatching {
                legacySaveManager.getSaveRAM(game)
            }.getOrElse { throw LoadException(SLoadError.Saves) }

            val quickSaveData = runCatching {
                val shouldDiscardSave =
                    !archiveCoherencyEngine.shouldDiscardAutoSaveState(game, coreBundle.eCoreType)

                if (coreBundle.isSupportStates && loadSave && shouldDiscardSave) {
                    legacySaveStateManager.getAutoSave(game, coreBundle.eCoreType)
                } else {
                    null
                }
            }.getOrElse { throw LoadException(SLoadError.Saves) }

            val coreVariables = legacyCorePropertyManager.getOptionsForCore(systemBundle.eSystemType, coreBundle)
                .toTypedArray()

            val systemDirectory = storageProvider.getInternalFileSystem()
            val savesDirectory = storageProvider.getExternalFileSaves()

            emit(
                SGameLoadState.LoadReady(
                    GameBundle(
                        game,
                        coreLibrary,
                        gameFiles,
                        quickSaveData,
                        saveRAMData,
                        coreVariables,
                        systemDirectory,
                        savesDirectory
                    ).also { UtilKLogWrapper.w(TAG, "GameData $it") }
                )
            )
        } catch (e: LoadException) {
            UtilKLogWrapper.e( TAG,"Error while preparing game",e)
            throw e
        } catch (e: Exception) {
            com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.e(TAG,"Error while preparing game",e)
            throw LoadException(SLoadError.Generic)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    private fun isArchitectureSupported(coreBundle: CoreBundle): Boolean {
        val supportedOnlyArchitectures = coreBundle.supportedOnlyArchitectures ?: return true
        return Build.SUPPORTED_ABIS.toSet().intersect(supportedOnlyArchitectures).isNotEmpty()
    }

    private fun findLibrary(context: Context, eCoreType: ECoreType): File? {
        val soFiles = sequenceOf(File(context.applicationInfo.nativeLibraryDir), context.filesDir)

        for (file in soFiles){
            UtilKLogWrapper.w(TAG, "findLibrary files ${file.listFiles()?.joinToString { it.absolutePath+"\n" }}")
        }
        return soFiles
            .flatMap { it.walkBottomUp() }
            .firstOrNull { it.name == eCoreType.coreSoFileName }
    }
}
