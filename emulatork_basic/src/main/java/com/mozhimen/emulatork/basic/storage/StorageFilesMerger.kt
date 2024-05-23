package com.mozhimen.emulatork.basic.storage

import android.net.Uri
import com.mozhimen.basick.utilk.java.io.inputStream2strs_use_ofBufferedReader_forEachLine

/**
 * @ClassName StorageFilesMerger
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object StorageFilesMerger {

    /** Merge files which belong to the same game. This includes bin/cue files and m3u playlists.*/
    fun mergeDataFiles(
        storageProvider: StorageProvider,
        files: List<StorageBaseFile>
    ): List<StorageGroupedFiles> {
        val allFiles = files
            .associateWith { listOf<StorageBaseFile>() }
            .toMutableMap()

        mergeBinCueFiles(allFiles, storageProvider)
        removeInvalidBinCuePairs(allFiles, storageProvider)
        mergeM3UPlaylists(allFiles, storageProvider)
        removeInvalidM3UPlaylists(allFiles, storageProvider)

        return allFiles.map { StorageGroupedFiles(it.key, it.value) }
    }

    /////////////////////////////////////////////////////////////////////////////////

    private fun removeInvalidM3UPlaylists(
        allFiles: MutableMap<StorageBaseFile, List<StorageBaseFile>>,
        storageProvider: StorageProvider
    ) {
        val toBeRemoved = mutableListOf<StorageBaseFile>()

        allFiles.keys
            .asSequence()
            .filter { it.extension == "m3u" }
            .forEach { m3uFile ->
                val m3uFiles: List<String> = runCatching {
                    storageProvider.getInputStream(m3uFile.uri)?.inputStream2strs_use_ofBufferedReader_forEachLine()
                }.getOrNull() ?: listOf()

                val filesNames = allFiles[m3uFile]?.map { it.name } ?: listOf()

                if (!filesNames.containsAll(m3uFiles)) {
                    toBeRemoved.add(m3uFile)
                }
            }

        toBeRemoved.forEach { allFiles.remove(it) }
    }

    private fun mergeM3UPlaylists(
        allFiles: MutableMap<StorageBaseFile, List<StorageBaseFile>>,
        storageProvider: StorageProvider
    ) {
        val toBeRemoved = mutableListOf<StorageBaseFile>()

        allFiles.keys
            .asSequence()
            .filter { it.extension == "m3u" }
            .forEach { m3uFile ->
                val m3uFiles = runCatching {
                    storageProvider.getInputStream(m3uFile.uri)?.inputStream2strs_use_ofBufferedReader_forEachLine()
                }.getOrNull() ?: listOf()

                val dataFiles = allFiles.filter { it.key.name in m3uFiles }

                allFiles[m3uFile] = allFiles[m3uFile]!! + dataFiles.flatMap {
                    listOf(it.key) + it.value
                }
                toBeRemoved.addAll(dataFiles.keys)
            }

        toBeRemoved.forEach { allFiles.remove(it) }
    }

    private fun removeInvalidBinCuePairs(
        allFiles: MutableMap<StorageBaseFile, List<StorageBaseFile>>,
        storageProvider: StorageProvider
    ) {
        val toBeRemoved = mutableListOf<StorageBaseFile>()

        allFiles.keys
            .asSequence()
            .filter { it.extension == "cue" }
            .forEach {
                val requestedFileNames = extractBinFiles(storageProvider, it.uri).toSet()
                val givenFileNames = allFiles[it]?.map { it.name }?.toSet() ?: setOf()

                if (requestedFileNames != givenFileNames) toBeRemoved.add(it)
            }

        toBeRemoved.forEach { allFiles.remove(it) }
    }

    private fun mergeBinCueFiles(
        allFiles: MutableMap<StorageBaseFile, List<StorageBaseFile>>,
        storageProvider: StorageProvider
    ) {
        val toBeRemoved = mutableListOf<StorageBaseFile>()

        allFiles.keys
            .asSequence()
            .filter { it.extension == "cue" }
            .forEach { cueFile ->
                val requestedBinFiles = extractBinFiles(storageProvider, cueFile.uri)

                val binFiles = allFiles
                    .filter { it.key.name in requestedBinFiles }

                allFiles[cueFile] = (allFiles[cueFile] ?: listOf()) + binFiles.flatMap {
                    listOf(it.key) + it.value
                }
                toBeRemoved.addAll(binFiles.keys)
            }

        toBeRemoved.forEach { allFiles.remove(it) }
    }

    private fun extractBinFiles(storageProvider: StorageProvider, uri: Uri): List<String> {
        return runCatching {
            storageProvider.getInputStream(uri)?.inputStream2strs_use_ofBufferedReader_forEachLine()
                ?.mapNotNull { Regex("FILE \"(.*)\"").find(it)?.groupValues?.get(1) }
                ?: listOf()
        }.getOrDefault(listOf())
    }
}
