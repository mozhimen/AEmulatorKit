package com.mozhimen.emulatork.basic.game

import android.content.Context
import android.os.Build
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.basic.core.CoreVariable
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import com.mozhimen.emulatork.basic.core.CoreID
import com.mozhimen.emulatork.basic.EmulatorKBasic
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.save.SaveState
import com.mozhimen.emulatork.basic.save.SaveCoherencyEngine
import com.mozhimen.emulatork.basic.save.SaveManager
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager
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
    private val legacyCoreVariablesManager: CoreVariablesManager,
    private val retrogradeDatabase: RetrogradeDatabase,
    private val saveCoherencyEngine: SaveCoherencyEngine,
    private val storageDirectoriesManager: StorageDirectoriesManager,
    private val biosManager: BiosManager
) : IUtilK {

    sealed class LoadingState {
        object LoadingCore : LoadingState()
        object LoadingGame : LoadingState()
        class Ready(val gameData: GameData) : LoadingState()
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
    ): Flow<LoadingState> = flow {
        try {
            emit(LoadingState.LoadingCore)

            val system = GameSystems.findById(game.systemId)

            if (!isArchitectureSupported(systemCoreConfig)) {
                throw GameLoaderException(GameLoaderError.UnsupportedArchitecture)
            }

            val coreLibrary = runCatching {
                findLibrary(appContext, systemCoreConfig.coreID)!!.absolutePath
            }.getOrElse {
                throw GameLoaderException(GameLoaderError.LoadCore)
            }

            emit(LoadingState.LoadingGame)

            val missingBiosFiles = biosManager.getMissingBiosFiles(systemCoreConfig, game)
            if (missingBiosFiles.isNotEmpty()) {
                throw GameLoaderException(GameLoaderError.MissingBiosFiles(missingBiosFiles))
            }

            val gameFiles = runCatching {
                val useVFS = systemCoreConfig.supportsLibretroVFS && directLoad
                val dataFiles = retrogradeDatabase.dataFileDao().selectDataFilesForGame(game.id)
                lemuroidLibrary.getGameFiles(game, dataFiles, useVFS)
            }.getOrElse { throw it }

            val saveRAMData = runCatching {
                legacySaveManager.getSaveRAM(game)
            }.getOrElse { throw GameLoaderException(GameLoaderError.Saves) }

            val quickSaveData = runCatching {
                val shouldDiscardSave =
                    !saveCoherencyEngine.shouldDiscardAutoSaveState(game, systemCoreConfig.coreID)

                if (systemCoreConfig.statesSupported && loadSave && shouldDiscardSave) {
                    legacySaveStateManager.getAutoSave(game, systemCoreConfig.coreID)
                } else {
                    null
                }
            }.getOrElse { throw GameLoaderException(GameLoaderError.Saves) }

            val coreVariables = legacyCoreVariablesManager.getOptionsForCore(system.id, systemCoreConfig)
                .toTypedArray()

            val systemDirectory = storageDirectoriesManager.getSystemDirectory()
            val savesDirectory = storageDirectoriesManager.getSavesDirectory()

            emit(
                LoadingState.Ready(
                    GameData(
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
        } catch (e: GameLoaderException) {
            Timber.e(e, "Error while preparing game")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Error while preparing game")
            throw GameLoaderException(GameLoaderError.Generic)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    private fun isArchitectureSupported(systemCoreConfig: GameSystemCoreConfig): Boolean {
        val supportedOnlyArchitectures = systemCoreConfig.supportedOnlyArchitectures ?: return true
        return Build.SUPPORTED_ABIS.toSet().intersect(supportedOnlyArchitectures).isNotEmpty()
    }

    private fun findLibrary(context: Context, coreID: CoreID): File? {
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
