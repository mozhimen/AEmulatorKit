package com.mozhimen.emulatork.common.game

import android.content.Context
import android.os.Build
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.core.ECoreId
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.load.LoadException
import com.mozhimen.emulatork.basic.save.SaveCoherencyEngine
import com.mozhimen.emulatork.basic.save.SaveManager
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.basic.load.SLoadError
import com.mozhimen.emulatork.common.EmulatorKBasic
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.common.core.CorePropertyManager
import com.mozhimen.emulatork.common.system.SystemProvider
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.db.game.entities.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File

/**
 * @ClassName GameLoader
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class GameLoadManager(
    private val lemuroidLibrary: EmulatorKBasic,
    private val legacySaveStateManager: SaveStateManager,
    private val legacySaveManager: SaveManager,
    private val legacyCorePropertyManager: CorePropertyManager,
    private val retrogradeDatabase: RetrogradeDatabase,
    private val saveCoherencyEngine: SaveCoherencyEngine,
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

            val system = SystemProvider.findSysByName(game.systemName)

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
                lemuroidLibrary.getGameFiles(game, dataFiles, useVFS)
            }.getOrElse { throw it }

            val saveRAMData = runCatching {
                legacySaveManager.getSaveRAM(game)
            }.getOrElse { throw LoadException(SLoadError.Saves) }

            val quickSaveData = runCatching {
                val shouldDiscardSave =
                    !saveCoherencyEngine.shouldDiscardAutoSaveState(game, coreBundle.coreID)

                if (coreBundle.statesSupported && loadSave && shouldDiscardSave) {
                    legacySaveStateManager.getAutoSave(game, coreBundle.coreID)
                } else {
                    null
                }
            }.getOrElse { throw LoadException(SLoadError.Saves) }

            val coreVariables = legacyCorePropertyManager.getOptionsForCore(system.id, coreBundle)
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
            Timber.e(e, "Error while preparing game")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Error while preparing game")
            throw LoadException(SLoadError.Generic)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    private fun isArchitectureSupported(systemCoreConfig: GameSystemCoreConfig): Boolean {
        val supportedOnlyArchitectures = systemCoreConfig.supportedOnlyArchitectures ?: return true
        return Build.SUPPORTED_ABIS.toSet().intersect(supportedOnlyArchitectures).isNotEmpty()
    }

    private fun findLibrary(context: Context, coreID: com.mozhimen.emulatork.core.ECoreId): File? {
        val files = sequenceOf(File(context.applicationInfo.nativeLibraryDir), context.filesDir)

        for (file in files){
            UtilKLogWrapper.w(TAG, "findLibrary files ${file.listFiles()?.joinToString { it.absolutePath+"\n" }}")
        }
        return files
            .flatMap { it.walkBottomUp() }
            .firstOrNull { it.name == coreID.libretroFileName }
    }
}
