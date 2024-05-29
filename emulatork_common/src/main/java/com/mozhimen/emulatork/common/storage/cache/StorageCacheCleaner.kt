package com.mozhimen.emulatork.common.storage.cache

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.system.Os
import android.text.format.Formatter
import com.mozhimen.basick.utilk.kotlin.gigaBytes
import com.mozhimen.basick.utilk.kotlin.megaBytes
import com.mozhimen.emulatork.basic.storage.local.StorageLocalAccessFrameworkProvider
import com.mozhimen.emulatork.basic.storage.local.StorageLocalProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * @ClassName CacheCleaner
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object StorageCacheCleaner {

    private val MIN_CACHE_LIMIT = 64L.megaBytes()
    private val MAX_CACHE_LIMIT = 10L.gigaBytes()

    //////////////////////////////////////////////////////////////////////////////////

    fun getSupportedCacheLimits(): List<Long> {
        return generateSequence(MIN_CACHE_LIMIT) { it * 2L }
            .takeWhile { it <= MAX_CACHE_LIMIT }
            .toList()
    }

    fun getDefaultCacheLimit(): Long {
        val defaultCacheSize = (getInternalMemorySize() * 0.01f).roundToLong()
        return getClosestCacheLimit(defaultCacheSize)
    }

    suspend fun cleanAll(appContext: Context): Unit = withContext(Dispatchers.IO) {
        Timber.i("Running cache cleanup everything task")
        appContext.cacheDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    suspend fun clean(
        appContext: Context,
        requestedLimit: Long
    ): Unit = withContext(Dispatchers.IO) {
        Timber.i("Running cache cleanup lru task")
        val cacheLimit = getClosestCacheLimit(requestedLimit)

        val cacheFoldersSequence = sequenceOf(
            File(appContext.cacheDir, StorageLocalAccessFrameworkProvider.SAF_CACHE_SUBFOLDER).walkBottomUp(),
            File(appContext.cacheDir, StorageLocalProvider.LOCAL_STORAGE_CACHE_SUBFOLDER).walkBottomUp()
        )

        val cacheFiles = cacheFoldersSequence.flatten()
            .filter { it.isFile }
            .sortedBy { retrieveLastAccess(it) }
            .toMutableList()

        val cacheSize = cacheFiles
            .map { it.length() }
            .sum()

        Timber.i("Space used by cache: ${printSize(appContext, cacheSize)} / ${printSize(appContext, cacheLimit)}")

        var spaceToBeDeleted = maxOf(cacheSize - cacheLimit, 0)

        Timber.i("Freeing cache space: ${printSize(appContext, spaceToBeDeleted)}")

        while (spaceToBeDeleted > 0) {
            val deletedFile = cacheFiles.removeAt(0)
            val size = deletedFile.length()

            if (deletedFile.delete()) {
                spaceToBeDeleted -= size
                Timber.i("Cache file deleted ${deletedFile.name}, size: ${printSize(appContext, size)}")
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    private fun getClosestCacheLimit(size: Long): Long {
        return getSupportedCacheLimits()
            .minByOrNull { abs(it - size) } ?: 0
    }

    private fun getInternalMemorySize(): Long {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.absolutePath)
        return stat.blockSizeLong * stat.blockCountLong
    }

    private fun printSize(appContext: Context, size: Long): String {
        return Formatter.formatFileSize(appContext, size)
    }

    private fun retrieveLastAccess(file: File) = Os.lstat(file.absolutePath).st_atime
}
