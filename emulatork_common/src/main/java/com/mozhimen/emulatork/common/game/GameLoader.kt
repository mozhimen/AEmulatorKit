package com.mozhimen.emulatork.common.game

import android.content.Context
import android.os.Build
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.core.variable.CoreVariable
import com.mozhimen.emulatork.core.CoreVariablesManager
import com.mozhimen.emulatork.core.ECoreId
import com.mozhimen.emulatork.basic.EmulatorKBasic
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.save.SaveState
import com.mozhimen.emulatork.basic.save.SaveCoherencyEngine
import com.mozhimen.emulatork.basic.save.SaveManager
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.basic.storage.StorageRomFile
import com.mozhimen.emulatork.basic.game.system.GameSystems
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
class GameLoader(
    private val lemuroidLibrary: EmulatorKBasic,
    private val legacySaveStateManager: SaveStateManager,
    private val legacySaveManager: SaveManager,
    private val legacyCoreVariablesManager: com.mozhimen.emulatork.core.CoreVariablesManager,
    private val retrogradeDatabase: RetrogradeDatabase,
    private val saveCoherencyEngine: SaveCoherencyEngine,
    private val storageProvider: StorageProvider,
    private val biosManager: BiosManager
) : IUtilK {

    sealed class LoadingState {
        object LoadingCore : com.mozhimen.emulatork.common.game.GameLoader.LoadingState()
        object LoadingGame : com.mozhimen.emulatork.common.game.GameLoader.LoadingState()
        class Ready(val gameData: com.mozhimen.emulatork.common.game.GameLoader.GameData) : com.mozhimen.emulatork.common.game.GameLoader.LoadingState()
    }

    @Suppress("ArrayInDataClass")
    data class GameData constructor(
        val game: Game,
        val coreLibrary: String,
        val gameFiles: StorageRomFile,
        val quickSaveData: SaveState?,
        val saveRAMData: ByteArray?,
        val coreVariables: Array<CoreVariable>,
        val systemDirectory: File,
        val savesDirectory: File
    )

    ///////////////////////////////////////////////////////////////////////////////

    fun load(
        appContext: Context,
        game: Game,
        loadSave: Boolean,
        systemCoreConfig: GameSystemCoreConfig,
        directLoad: Boolean
    ): Flow<com.mozhimen.emulatork.common.game.GameLoader.LoadingState> = flow {
        try {
            emit(com.mozhimen.emulatork.common.game.GameLoader.LoadingState.LoadingCore)

            val system = GameSystems.findById(game.systemId)

            if (!isArchitectureSupported(systemCoreConfig)) {
                throw com.mozhimen.emulatork.common.game.GameLoaderException(com.mozhimen.emulatork.common.game.GameLoaderError.UnsupportedArchitecture)
            }

            val coreLibrary = runCatching {
                findLibrary(appContext, systemCoreConfig.coreID)!!.absolutePath
            }.getOrElse {
                throw com.mozhimen.emulatork.common.game.GameLoaderException(com.mozhimen.emulatork.common.game.GameLoaderError.LoadCore)
            }

            emit(com.mozhimen.emulatork.common.game.GameLoader.LoadingState.LoadingGame)

            val missingBiosFiles = biosManager.getMissingBiosFiles(systemCoreConfig, game)
            if (missingBiosFiles.isNotEmpty()) {
                throw com.mozhimen.emulatork.common.game.GameLoaderException(com.mozhimen.emulatork.common.game.GameLoaderError.MissingBiosFiles(missingBiosFiles))
            }

            val gameFiles = runCatching {
                val useVFS = systemCoreConfig.supportsLibretroVFS && directLoad
                val dataFiles = retrogradeDatabase.dataFileDao().selectDataFilesForGame(game.id)
                lemuroidLibrary.getGameFiles(game, dataFiles, useVFS)
            }.getOrElse { throw it }

            val saveRAMData = runCatching {
                legacySaveManager.getSaveRAM(game)
            }.getOrElse { throw com.mozhimen.emulatork.common.game.GameLoaderException(com.mozhimen.emulatork.common.game.GameLoaderError.Saves) }

            val quickSaveData = runCatching {
                val shouldDiscardSave =
                    !saveCoherencyEngine.shouldDiscardAutoSaveState(game, systemCoreConfig.coreID)

                if (systemCoreConfig.statesSupported && loadSave && shouldDiscardSave) {
                    legacySaveStateManager.getAutoSave(game, systemCoreConfig.coreID)
                } else {
                    null
                }
            }.getOrElse { throw com.mozhimen.emulatork.common.game.GameLoaderException(com.mozhimen.emulatork.common.game.GameLoaderError.Saves) }

            val coreVariables = legacyCoreVariablesManager.getOptionsForCore(system.id, systemCoreConfig)
                .toTypedArray()

            val systemDirectory = storageProvider.getInternalFileSystem()
            val savesDirectory = storageProvider.getExternalFileSaves()

            emit(
                com.mozhimen.emulatork.common.game.GameLoader.LoadingState.Ready(
                    com.mozhimen.emulatork.common.game.GameLoader.GameData(
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
        } catch (e: com.mozhimen.emulatork.common.game.GameLoaderException) {
            Timber.e(e, "Error while preparing game")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Error while preparing game")
            throw com.mozhimen.emulatork.common.game.GameLoaderException(com.mozhimen.emulatork.common.game.GameLoaderError.Generic)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    private fun isArchitectureSupported(systemCoreConfig: GameSystemCoreConfig): Boolean {
        val supportedOnlyArchitectures = systemCoreConfig.supportedOnlyArchitectures ?: return true
        return Build.SUPPORTED_ABIS.toSet().intersect(supportedOnlyArchitectures).isNotEmpty()
    }

    private fun findLibrary(context: Context, coreID: com.mozhimen.emulatork.core.ECoreId): File? {
        val files = sequenceOf(
            File(context.applicationInfo.nativeLibraryDir),
            context.filesDir
        )

        for (file in files){
            UtilKLogWrapper.w(TAG, "findLibrary files ${file.listFiles()?.joinToString { it.absolutePath+"\n" }}")
        }
        return files
            .flatMap { it.walkBottomUp() }
            .firstOrNull { it.name == coreID.libretroFileName }
    }
}
