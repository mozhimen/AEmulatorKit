package com.mozhimen.emulatork.basic.library

import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.library.db.mos.DataFile
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.basic.library.metadata.GameMetadata
import com.mozhimen.emulatork.basic.library.metadata.GameMetadataProvider
import com.mozhimen.emulatork.basic.storage.BaseStorageFile
import com.mozhimen.emulatork.basic.storage.GroupedStorageFiles
import com.mozhimen.emulatork.basic.storage.RomFiles
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.basic.storage.StorageProviderRegistry
import com.mozhimen.emulatork.util.coroutines.batchWithSizeAndTime
import dagger.Lazy
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber

/**
 * @ClassName LemuroidLibrary
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class LemuroidLibrary(
    private val retrogradedb: RetrogradeDatabase,
    private val storageProviderRegistry: Lazy<StorageProviderRegistry>,
    private val gameMetadataProvider: Lazy<GameMetadataProvider>,
    private val biosManager: BiosManager
) {

    suspend fun indexLibrary() {
        val startedAtMs = System.currentTimeMillis()

        try {
            indexProviders(startedAtMs)
        } catch (e: Throwable) {
            Timber.e("Library indexing stopped due to exception", e)
        } finally {
            cleanUp(startedAtMs)
        }

        val executionTime = System.currentTimeMillis() - startedAtMs
        Timber.i("Library indexing completed in: $executionTime ms")
    }

    @OptIn(FlowPreview::class)
    private suspend fun indexProviders(startedAtMs: Long) {
        val gameMetadata = gameMetadataProvider.get()
        val enabledProviders = storageProviderRegistry.get().enabledProviders
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
        gameMetadata: GameMetadataProvider
    ): Flow<Unit> {
        return provider.listBaseStorageFiles()
            .flatMapConcat { StorageFilesMerger.mergeDataFiles(provider, it).asFlow() }
            .batchWithSizeAndTime(MAX_BUFFER_SIZE, MAX_TIME)
            .flatMapMerge {
                processBatch(it, provider, startedAtMs, gameMetadata)
            }
    }

    private suspend fun processBatch(
        batch: List<GroupedStorageFiles>,
        provider: StorageProvider,
        startedAtMs: Long,
        gameMetadata: GameMetadataProvider
    ) = flow<Unit> {
        val entries = batch.map { fetchEntriesFromDatabase(it) }

        val existingEntries = entries.filterIsInstance<ScanEntry.GameFile>()
        handleExistingEntries(existingEntries, startedAtMs)

        val newEntries = entries.filterIsInstance<ScanEntry.File>()
            .map { buildEntryFromMetadata(it.file, provider, gameMetadata, startedAtMs) }

        handleNewEntries(newEntries, startedAtMs, provider)
    }

    private fun fetchEntriesFromDatabase(storageFile: GroupedStorageFiles): ScanEntry {
        val game = retrogradedb.gameDao().selectByFileUri(storageFile.primaryFile.uri.toString())
        Timber.d("Retrieving scan entry game $game for uri: ${storageFile.primaryFile}")
        return buildScanEntry(storageFile, game)
    }

    private fun buildScanEntry(storageFile: GroupedStorageFiles, game: Game?): ScanEntry {
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
            .forEach { Timber.d("Updating game: $it") }

        retrogradedb.gameDao().update(updatedGames)
    }

    private fun updateDataFiles(entries: List<ScanEntry.GameFile>, startedAtMs: Long) {
        val dataFiles = entries.flatMap { (storageFile, game) ->
            storageFile.dataFiles.map { convertIntoDataFile(game.id, it, startedAtMs) }
        }

        dataFiles
            .forEach { Timber.d("Updating data file: $it") }

        retrogradedb.dataFileDao().insert(dataFiles)
    }

    private fun convertIntoDataFile(
        gameId: Int,
        baseStorageFile: BaseStorageFile,
        startedAtMs: Long
    ): DataFile {
        return DataFile(
            gameId = gameId,
            fileUri = baseStorageFile.uri.toString(),
            fileName = baseStorageFile.name,
            lastIndexedAt = startedAtMs,
            path = baseStorageFile.path
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

        games.forEach { Timber.d("Insert: $it") }

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
        files: List<BaseStorageFile>,
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
        groupedStorageFile: GroupedStorageFiles,
        provider: StorageProvider,
        metadataProvider: GameMetadataProvider,
        startedAtMs: Long
    ): ScanEntry {
        val game = sortedFilesForScanning(groupedStorageFile).asFlow()
            .mapNotNull { safeStorageFile(provider, it) }
            .mapNotNull { storageFile ->
                val metadata = metadataProvider.retrieveMetadata(storageFile)
                convertGameMetadataToGame(groupedStorageFile, storageFile, metadata, startedAtMs)
            }
            .firstOrNull()

        return buildScanEntry(groupedStorageFile, game)
    }

    private fun safeStorageFile(
        provider: StorageProvider,
        baseStorageFile: BaseStorageFile
    ): StorageFile? {
        return runCatching { provider.getStorageFile(baseStorageFile) }
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

    private fun sortedFilesForScanning(groupedStorageFile: GroupedStorageFiles): List<BaseStorageFile> {
        return groupedStorageFile.dataFiles.sortedBy { it.name } + listOf(groupedStorageFile.primaryFile)
    }

    private fun convertGameMetadataToGame(
        groupedStorageFile: GroupedStorageFiles,
        storageFile: StorageFile,
        gameMetadata: GameMetadata?,
        lastIndexedAt: Long
    ): Game? {

        if (gameMetadata == null) {
            return null
        }

        val gameSystem = GameSystem.findById(gameMetadata.system!!)

        // If the databased matched a data file (as with bin/cue) we force link the primary filename
        val fileName = if (groupedStorageFile.dataFiles.isNotEmpty()) {
            groupedStorageFile.primaryFile.name
        } else {
            storageFile.name
        }

        return Game(
            fileName = fileName,
            fileUri = groupedStorageFile.primaryFile.uri.toString(),
            title = gameMetadata.name ?: groupedStorageFile.primaryFile.name,
            systemId = gameSystem.id.dbname,
            developer = gameMetadata.developer,
            coverFrontUrl = gameMetadata.thumbnail,
            lastIndexedAt = lastIndexedAt
        )
    }

    private fun removeDeletedDataFiles(startedAtMs: Long) {
        val dataFiles = retrogradedb.dataFileDao().selectByLastIndexedAtLessThan(startedAtMs)
        Timber.d("Deleting data files from db before: $startedAtMs games $dataFiles")
        retrogradedb.dataFileDao().delete(dataFiles)
    }

    private fun removeDeletedGames(startedAtMs: Long) {
        val games = retrogradedb.gameDao().selectByLastIndexedAtLessThan(startedAtMs)
        Timber.d("Deleting games from db before: $startedAtMs games $games")
        retrogradedb.gameDao().delete(games)
    }

    fun getGameFiles(
        game: Game,
        dataFiles: List<DataFile>,
        allowVirtualFiles: Boolean
    ): RomFiles {
        val provider = storageProviderRegistry.get()
        return provider.getProvider(game).getGameRomFiles(game, dataFiles, allowVirtualFiles)
    }

    private sealed class ScanEntry {
        data class GameFile(val file: GroupedStorageFiles, val game: Game) : ScanEntry()
        data class File(val file: GroupedStorageFiles) : ScanEntry()
    }

    companion object {
        // We batch database updates to avoid unnecessary UI updates.
        const val MAX_BUFFER_SIZE = 200
        const val MAX_TIME = 5000
    }
}
