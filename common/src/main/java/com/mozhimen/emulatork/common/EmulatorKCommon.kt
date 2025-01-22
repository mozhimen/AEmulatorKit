package com.mozhimen.emulatork.common

import android.util.Log
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.kotlinx.coroutines.batch_ofSizeTime
import com.mozhimen.emulatork.basic.metadata.Metadata
import com.mozhimen.emulatork.basic.metadata.MetadataProvider
import com.mozhimen.emulatork.basic.rom.SRomFileType
import com.mozhimen.emulatork.basic.storage.StorageBaseFile
import com.mozhimen.emulatork.basic.storage.StorageFileGroup
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.common.storage.StorageFileMerger
import com.mozhimen.emulatork.common.bios.BiosManager
import com.mozhimen.emulatork.common.storage.StorageProvider
import com.mozhimen.emulatork.common.storage.StorageProviderRegistry
import com.mozhimen.emulatork.common.system.SystemProvider
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.db.game.entities.GameDataFile
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull

/**
 * @ClassName LemuroidLibrary
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
open class EmulatorKCommon(
    private val retrogradedb: RetrogradeDatabase,
    private val storageProviderRegistry: Lazy<StorageProviderRegistry>,
    private val metadataProvider: Lazy<MetadataProvider>,
    private val biosManager: BiosManager
) : IUtilK {

    companion object {
        // We batch database updates to avoid unnecessary UI updates.
        const val MAX_BUFFER_SIZE = 200
        const val MAX_TIME = 5000
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    fun getGameFiles(
        game: Game,
        dataFiles: List<GameDataFile>,
        allowVirtualFiles: Boolean
    ): SRomFileType {
        val provider = storageProviderRegistry.value
        return provider.getStorageProvider(game).getGameRomFiles(game, dataFiles, allowVirtualFiles)
    }

    suspend fun indexLibrary() {
        val startedAtMs = System.currentTimeMillis()

        try {
            indexProviders(startedAtMs)
        } catch (e: Throwable) {
            e.printStackTrace()
            com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.e(TAG,"Library indexing stopped due to exception", e)
        } finally {
            cleanUp(startedAtMs)
        }

        val executionTime = System.currentTimeMillis() - startedAtMs
         com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.i(TAG,"Library indexing completed in: $executionTime ms")
    }

    //////////////////////////////////////////////////////////////////////////////////////

    private sealed class ScanEntry {
        data class GameFile(val file: StorageFileGroup, val game: Game) : ScanEntry()
        data class File(val file: StorageFileGroup) : ScanEntry()
    }

    @OptIn(FlowPreview::class)
    private suspend fun indexProviders(startedAtMs: Long) {
        val gameMetadata = metadataProvider.value
        val enabledProviders = storageProviderRegistry.value.enabledProviders
        enabledProviders.asFlow()
            .flatMapConcat {
                indexSingleProvider(it, startedAtMs, gameMetadata)
            }
            .collect()
    }

    @OptIn(FlowPreview::class)
    private fun indexSingleProvider(
        provider: StorageProvider,
        startedAtMs: Long,
        gameMetadata: MetadataProvider
    ): Flow<Unit> {
        return provider.listStorageBaseFiles()
            .flatMapConcat { StorageFileMerger.mergeDataFiles(provider, it).asFlow() }
            .batch_ofSizeTime(MAX_BUFFER_SIZE, MAX_TIME)
            .flatMapMerge {
                processBatch(it, provider, startedAtMs, gameMetadata)
            }
    }

    private suspend fun processBatch(
        batch: List<StorageFileGroup>,
        provider: StorageProvider,
        startedAtMs: Long,
        gameMetadata: MetadataProvider
    ) = flow<Unit> {
        val entries = batch.map { fetchEntriesFromDatabase(it) }

        val existingEntries = entries.filterIsInstance<ScanEntry.GameFile>()
        handleExistingEntries(existingEntries, startedAtMs)

        val newEntries = entries.filterIsInstance<ScanEntry.File>()
            .map { buildEntryFromMetadata(it.file, provider, gameMetadata, startedAtMs) }

        handleNewEntries(newEntries, startedAtMs, provider)
    }

    private fun fetchEntriesFromDatabase(storageFile: StorageFileGroup): ScanEntry {
        val game = retrogradedb.gameDao().selectByFileUri(storageFile.primaryFile.uri.toString())
        com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Retrieving scan entry game $game for uri: ${storageFile.primaryFile}")
        return buildScanEntry(storageFile, game)
    }

    private fun buildScanEntry(storageFile: StorageFileGroup, game: Game?): ScanEntry {
         com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.w(TAG,"buildScanEntry $game")
        return if (game != null) {
            ScanEntry.GameFile(storageFile, game)
        } else {
            ScanEntry.File(storageFile)
        }
    }

    private fun handleExistingEntries(entries: List<ScanEntry.GameFile>, startedAtMs: Long) {
        updateGames(entries, startedAtMs)
        updateDataFiles(entries, startedAtMs)
    }

    private fun updateGames(entries: List<ScanEntry.GameFile>, startedAtMs: Long) {
        val updatedGames = entries
            .map { it.game.copy(lastIndexedAt = startedAtMs) }

        updatedGames
            .forEach { com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Updating game: $it") }

        retrogradedb.gameDao().update(updatedGames)
    }

    private fun updateDataFiles(entries: List<ScanEntry.GameFile>, startedAtMs: Long) {
        val dataFiles = entries.flatMap { (storageFile, game) ->
            storageFile.dataFiles.map { convertIntoDataFile(game.id, it, startedAtMs) }
        }

        dataFiles
            .forEach { com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Updating data file: $it") }

        retrogradedb.dataFileDao().insert(dataFiles)
    }

    private fun convertIntoDataFile(
        gameId: Int,
        storageBaseFile: StorageBaseFile,
        startedAtMs: Long
    ): GameDataFile {
        return GameDataFile(
            gameId = gameId,
            fileUri = storageBaseFile.uri.toString(),
            fileName = storageBaseFile.name,
            lastIndexedAt = startedAtMs,
            path = storageBaseFile.path
        )
    }

    private fun handleNewEntries(
        entries: List<ScanEntry>,
        startedAtMs: Long,
        provider: StorageProvider
    ) {
        val gameFiles = entries
            .filterIsInstance<ScanEntry.GameFile>()

        val unknownFiles = entries
            .filterIsInstance<ScanEntry.File>()
            .flatMap { it.file.allFiles() }

        handleNewGames(gameFiles, startedAtMs)
        handleUnknownFiles(provider, unknownFiles, startedAtMs)
    }

    private fun handleNewGames(pairs: List<ScanEntry.GameFile>, startedAtMs: Long) {
        val games = pairs
            .map { it.game }

        games.forEach { com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Insert: $it") }

        val gameIds = retrogradedb.gameDao().insert(games)
        val dataFiles = pairs
            .map { it.file.dataFiles }
            .zip(gameIds)
            .flatMap { (files, gameId) ->
                files.map {
                    convertIntoDataFile(gameId.toInt(), it, startedAtMs)
                }
            }

        retrogradedb.dataFileDao().insert(dataFiles)
    }

    private fun handleUnknownFiles(
        provider: StorageProvider,
        files: List<StorageBaseFile>,
        startedAtMs: Long
    ) {
        files.forEach { baseStorageFile ->
            val storageFile = safeStorageFile(provider, baseStorageFile)
            val inputStream = storageFile?.uri?.let { provider.getInputStream(it) }

            if (storageFile != null && inputStream != null) {
                biosManager.tryAddBiosAfter(storageFile, inputStream, startedAtMs)
            }
        }
    }

    private suspend fun buildEntryFromMetadata(
        groupedStorageFile: StorageFileGroup,
        provider: StorageProvider,
        metadataProvider: MetadataProvider,
        startedAtMs: Long
    ): ScanEntry {
        val game = sortedFilesForScanning(groupedStorageFile).asFlow()
            .mapNotNull {
                safeStorageFile(provider, it)
            }
            .mapNotNull { storageFile ->
                val metadata = metadataProvider.retrieveMetadata(storageFile)
                convertGameMetadataToGame(groupedStorageFile, storageFile, metadata, startedAtMs)
            }
            .firstOrNull()

        return buildScanEntry(groupedStorageFile, game)
    }

    private fun safeStorageFile(
        provider: StorageProvider,
        storageBaseFile: StorageBaseFile
    ): StorageFile? {
        return runCatching {
            provider.getStorageFile(storageBaseFile)
        }.apply {
            Log.w(TAG, "safeStorageFile $this")
        }
            .getOrNull()
    }

    private fun cleanUp(startedAtMs: Long) {
        kotlin.runCatching {
            removeDeletedBios(startedAtMs)
        }
        kotlin.runCatching {
            removeDeletedGames(startedAtMs)
        }
        kotlin.runCatching {
            removeDeletedDataFiles(startedAtMs)
        }
    }

    private fun removeDeletedBios(startedAtMs: Long) {
        biosManager.deleteBiosBefore(startedAtMs)
    }

    private fun sortedFilesForScanning(groupedStorageFile: StorageFileGroup): List<StorageBaseFile> {
        return groupedStorageFile.dataFiles.sortedBy { it.name } + listOf(groupedStorageFile.primaryFile)
    }

    private fun convertGameMetadataToGame(
        groupedStorageFile: StorageFileGroup,
        storageFile: StorageFile,
        metadata: Metadata?,
        lastIndexedAt: Long
    ): Game? {

        if (metadata == null) {
            return null
        }

        val gameSystem = SystemProvider.findSysByName(metadata.system!!)

        // If the databased matched a data file (as with bin/cue) we force link the primary filename
        val fileName = if (groupedStorageFile.dataFiles.isNotEmpty()) {
            groupedStorageFile.primaryFile.name
        } else {
            storageFile.name
        }

        return Game(
            fileName = fileName,
            fileUri = groupedStorageFile.primaryFile.uri.toString(),
            title = metadata.name ?: groupedStorageFile.primaryFile.name,
            systemName = gameSystem.eSystemType.simpleName,
            developer = metadata.developer,
            coverFrontUrl = metadata.thumbnail,
            lastIndexedAt = lastIndexedAt
        )
    }

    private fun removeDeletedDataFiles(startedAtMs: Long) {
        val dataFiles = retrogradedb.dataFileDao().selectByLastIndexedAtLessThan(startedAtMs)
        com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Deleting data files from db before: $startedAtMs games $dataFiles")
        retrogradedb.dataFileDao().delete(dataFiles)
    }

    private fun removeDeletedGames(startedAtMs: Long) {
        val games = retrogradedb.gameDao().selectByLastIndexedAtLessThan(startedAtMs)
        com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Deleting games from db before: $startedAtMs games $games")
        retrogradedb.gameDao().delete(games)
    }
}
