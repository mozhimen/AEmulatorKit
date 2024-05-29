package com.mozhimen.emulatork.common.save

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import com.mozhimen.emulatork.core.ECoreId
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.storage.StorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * @ClassName StatesPreviewManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class SaveStatePreviewManager(private val storageProvider: StorageProvider) {
    companion object {
        val PREVIEW_SIZE_DP = 96f
    }

    //////////////////////////////////////////////////////////////////////////

    suspend fun getPreviewForSlot(
        game: Game,
        coreID: com.mozhimen.emulatork.core.ECoreId,
        index: Int,
        size: Int
    ): Bitmap? = withContext(Dispatchers.IO) {
        val screenshotName = getSlotScreenshotName(game, index)
        val file = getPreviewFile(screenshotName, coreID.coreName)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        ThumbnailUtils.extractThumbnail(bitmap, size, size)
    }

    suspend fun setPreviewForSlot(
        game: Game,
        bitmap: Bitmap,
        coreID: com.mozhimen.emulatork.core.ECoreId,
        index: Int
    ) = withContext(Dispatchers.IO) {
        val screenshotName = getSlotScreenshotName(game, index)
        val file = getPreviewFile(screenshotName, coreID.coreName)
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
    }

    //////////////////////////////////////////////////////////////////////////

    private fun getPreviewFile(fileName: String, coreName: String): File {
        val statesDirectories = File(storageProvider.getExternalFileStatePreviews(), coreName)
        statesDirectories.mkdirs()
        return File(statesDirectories, fileName)
    }

    private fun getSlotScreenshotName(
        game: Game,
        index: Int
    ) = "${game.fileName}.slot${index + 1}.jpg"
}
