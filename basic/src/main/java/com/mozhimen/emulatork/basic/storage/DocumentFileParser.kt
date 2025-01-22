package com.mozhimen.emulatork.basic.storage

import android.content.Context
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.java.io.getStrCrc32_use
import com.mozhimen.basick.utilk.kotlin.long2strCrc32
import com.mozhimen.emulatork.basic.system.SystemSerialExtractor

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @ClassName DocumentFileParser
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object DocumentFileParser : IUtilK {

    private const val MAX_CHECKED_ENTRIES = 3
    private const val SINGLE_ARCHIVE_THRESHOLD = 0.9
    private const val MAX_SIZE_CRC32 = 1_000_000_000

    ////////////////////////////////////////////////////////////////////////////////

    fun parseDocumentFile(context: Context, storageBaseFile: StorageBaseFile): StorageFile {
        return if (storageBaseFile.extension == "zip") {
            com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Detected zip file. ${storageBaseFile.name}")
            parseZipFile(context, storageBaseFile)
        } else {
            com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Detected standard file. ${storageBaseFile.name}")
            parseStandardFile(context, storageBaseFile)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    /* Finds a zip entry which we assume is a game. Lemuroid only supports single archive games,
so we are looking for an entry which occupies a large percentage of the archive space.
This is very fast heuristic to compute and avoids reading the whole stream in most
scenarios.*/
    private fun findGameEntry(openedInputStream: ZipInputStream, fileSize: Long = -1): ZipEntry? {
        for (i in 0..MAX_CHECKED_ENTRIES) {
            val entry = openedInputStream.nextEntry ?: break
            if (!isGameEntry(entry, fileSize)) continue
            return entry
        }
        return null
    }

    private fun parseZipFile(context: Context, storageBaseFile: StorageBaseFile): StorageFile {
        val inputStream = context.contentResolver.openInputStream(storageBaseFile.uri)
        return ZipInputStream(inputStream).use {
            val gameEntry = findGameEntry(it, storageBaseFile.size)
            if (gameEntry != null) {
                com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Handing zip file as compressed game: ${storageBaseFile.name}")
                parseCompressedGame(storageBaseFile, gameEntry, it)
            } else {
                com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Handing zip file as standard: ${storageBaseFile.name}")
                parseStandardFile(context, storageBaseFile)
            }
        }
    }

    private fun parseCompressedGame(storageBaseFile: StorageBaseFile, entry: ZipEntry, zipInputStream: ZipInputStream): StorageFile {
        com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Processing zipped entry: ${entry.name}")

        val systemSerial = SystemSerialExtractor.extractInfo(entry.name, zipInputStream)

        return StorageFile(
            entry.name,
            entry.size,
            entry.crc.long2strCrc32(),
            systemSerial.serial,
            storageBaseFile.uri,
            storageBaseFile.uri.path,
            systemSerial.eSystemType
        )
    }

    private fun parseStandardFile(context: Context, storageBaseFile: StorageBaseFile): StorageFile {
        val diskInfo = context.contentResolver.openInputStream(storageBaseFile.uri)
            ?.let { inputStream -> SystemSerialExtractor.extractInfo(storageBaseFile.name, inputStream) }

        val crc32 = if (storageBaseFile.size < MAX_SIZE_CRC32 && diskInfo?.serial == null) {
            context.contentResolver.openInputStream(storageBaseFile.uri)?.getStrCrc32_use()
        } else {
            null
        }

        com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Parsed standard file: $storageBaseFile")

        return StorageFile(
            storageBaseFile.name,
            storageBaseFile.size,
            crc32,
            diskInfo?.serial,
            storageBaseFile.uri,
            storageBaseFile.uri.path,
            diskInfo?.eSystemType
        )
    }

    private fun isGameEntry(entry: ZipEntry, fileSize: Long): Boolean {
        if (fileSize <= 0 || entry.compressedSize <= 0) return false
        return (entry.compressedSize.toFloat() / fileSize.toFloat()) > SINGLE_ARCHIVE_THRESHOLD
    }
}
