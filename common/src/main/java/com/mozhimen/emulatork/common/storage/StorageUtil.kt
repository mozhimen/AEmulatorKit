package com.mozhimen.emulatork.common.storage

import android.content.Context
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.db.game.entities.GameDataFile
import java.io.File

/**
 * @ClassName GameCacheUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object StorageUtil {

    fun getDataFileForGame(folderName: String, context: Context, game: Game, dataFile: GameDataFile): File {
        val gamesCacheDir = getCacheDirForGame(folderName, game, context)
        return File(gamesCacheDir, dataFile.fileName)
    }

    fun getCacheFileForGame(folderName: String, context: Context, game: Game): File {
        val gamesCacheDir = getCacheDirForGame(folderName, game, context)
        return File(gamesCacheDir, game.fileName)
    }

    /////////////////////////////////////////////////////////////////

    private fun getCacheDirForGame(folderName: String, game: Game, context: Context): File {
        val gamesCachePath = buildPath(folderName, game.systemName)
        val gamesCacheDir = File(context.cacheDir, gamesCachePath)
        gamesCacheDir.mkdirs()
        return gamesCacheDir
    }

    private fun buildPath(vararg chunks: String): String {
        return chunks.joinToString(separator = File.separator)
    }
}
