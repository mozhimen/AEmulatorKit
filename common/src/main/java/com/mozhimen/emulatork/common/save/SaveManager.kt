package com.mozhimen.emulatork.common.save

import com.mozhimen.basick.utilk.kotlin.UtilKResult
import com.mozhimen.emulatork.basic.archive.ArchiveInfo
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.db.game.entities.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @ClassName SavesManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class SaveManager(private val storageProvider: StorageDirProvider) {
    companion object {
        private const val FILE_ACCESS_RETRIES = 3
    }

    ///////////////////////////////////////////////////////////////////

    suspend fun getSaveRAM(game: Game): ByteArray? =
        withContext(Dispatchers.IO) {
            val result = UtilKResult.runCatching_ofRetry(FILE_ACCESS_RETRIES) {
                val saveFile = getSaveFile(getSaveRAMFileName(game))
                if (saveFile.exists() && saveFile.length() > 0) {
                    saveFile.readBytes()
                } else {
                    null
                }
            }
            result.getOrNull()
        }

    suspend fun setSaveRAM(game: Game, data: ByteArray): Unit =
        withContext(Dispatchers.IO) {
            val result = UtilKResult.runCatching_ofRetry(FILE_ACCESS_RETRIES) {
                if (data.isEmpty())
                    return@runCatching_ofRetry

                val saveFile = getSaveFile(getSaveRAMFileName(game))
                saveFile.writeBytes(data)
            }
            result.getOrNull()
        }

    suspend fun getSaveRAMInfo(game: Game): ArchiveInfo =
        withContext(Dispatchers.IO) {
            val saveFile = getSaveFile(getSaveRAMFileName(game))
            val fileExists = saveFile.exists() && saveFile.length() > 0
            ArchiveInfo(fileExists, saveFile.lastModified())
        }

    ///////////////////////////////////////////////////////////////////

    private suspend fun getSaveFile(fileName: String): File =
        withContext(Dispatchers.IO) {
            val savesDirectory = storageProvider.getExternalFileSaves()
            File(savesDirectory, fileName)
        }

    /** This name should make it compatible with RetroArch so that users can freely sync saves across the two application. */
    private fun getSaveRAMFileName(game: Game) = "${game.fileName.substringBeforeLast(".")}.srm"
}
