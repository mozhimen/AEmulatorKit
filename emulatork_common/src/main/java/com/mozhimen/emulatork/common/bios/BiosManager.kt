package com.mozhimen.emulatork.common.bios

import com.mozhimen.basick.utilk.java.io.deleteFile
import com.mozhimen.basick.utilk.java.io.inputStream2file_use_ofCopyTo
import com.mozhimen.basick.utilk.kotlin.collections.associateByNotNull
import com.mozhimen.emulatork.basic.bios.Bios
import com.mozhimen.emulatork.basic.bios.BiosProcessor
import com.mozhimen.emulatork.basic.bios.BiosProvider
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.db.game.entities.Game
import timber.log.Timber
import java.io.File
import java.io.InputStream

/**
 * @ClassName BiosManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class BiosManager(private val storageProvider: StorageDirProvider) {

    private val crcLookup = BiosProvider.getBioss().associateByNotNull { it.externalCRC32 }
    private val nameLookup = BiosProvider.getBioss().associateByNotNull { it.externalName }

    /////////////////////////////////////////////////////////////////////////////

    fun getMissingBiosFiles(coreBundle: CoreBundle, game: Game): List<String> {
        val regionalBiosFiles = coreBundle.regionalBIOSFiles

        val gameLabels = Regex("\\([A-Za-z]+\\)")
            .findAll(game.title)
            .map { it.value.drop(1).dropLast(1) }
            .filter { it.isNotBlank() }
            .toSet()

        Timber.d("Found game labels: $gameLabels")

        val requiredRegionalFiles = gameLabels.intersect(regionalBiosFiles.keys)
            .ifEmpty { regionalBiosFiles.keys }
            .mapNotNull { regionalBiosFiles[it] }

        Timber.d("Required regional files for game: $requiredRegionalFiles")

        return (coreBundle.requiredBIOSFiles + requiredRegionalFiles)
            .filter { !File(storageProvider.getInternalFileSystem(), it).exists() }
    }

    fun deleteBiosBefore(timestampMs: Long) {
        Timber.i("Pruning old bios files")
        BiosProvider.getBioss()
            .map { File(storageProvider.getInternalFileSystem(), it.libretroFileName) }
            .filter { it.lastModified() < normalizeTimestamp(timestampMs) }
            .forEach {
                Timber.d("Pruning old bios file: ${it.path}")
                it.deleteFile()
            }
    }

    fun getBiosInfo(): BiosProcessor {
        val bios = BiosProvider.getBioss().groupBy {
            File(storageProvider.getInternalFileSystem(), it.libretroFileName).exists()
        }.withDefault { listOf() }

        return BiosProcessor(bios.getValue(true), bios.getValue(false))
    }

    fun tryAddBiosAfter(
        storageFile: StorageFile,
        inputStream: InputStream,
        timestampMs: Long
    ): Boolean {
        val bios = findByCRC(storageFile) ?: findByName(storageFile) ?: return false

        Timber.i("Importing bios file: $bios")

        val biosFile = File(storageProvider.getInternalFileSystem(), bios.libretroFileName)
        if (biosFile.exists() && biosFile.setLastModified(normalizeTimestamp(timestampMs))) {
            Timber.d("Bios file already present. Updated last modification date.")
        } else {
            Timber.d("Bios file not available. Copying new file.")
            inputStream.inputStream2file_use_ofCopyTo(biosFile)
        }
        return true
    }

    /////////////////////////////////////////////////////////////////////////////

    private fun findByCRC(storageFile: StorageFile): Bios? {
        return crcLookup[storageFile.crc]
    }

    private fun findByName(storageFile: StorageFile): Bios? {
        return nameLookup[storageFile.name]
    }

    private fun normalizeTimestamp(timestamp: Long) = (timestamp / 1000) * 1000
}
