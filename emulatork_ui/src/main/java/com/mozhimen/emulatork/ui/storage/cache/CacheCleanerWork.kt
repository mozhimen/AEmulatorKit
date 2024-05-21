package com.mozhimen.emulatork.ui.storage.cache

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mozhimen.emulatork.basic.storage.cache.CacheCleaner
import com.mozhimen.emulatork.ui.settings.SettingsManager
import timber.log.Timber

/**
 * @ClassName CacheCleanerWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class CacheCleanerWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    //    @Inject
//    lateinit var settingsManager: SettingsManager
    abstract fun settingsManager(): SettingsManager

    override suspend fun doWork(): Result {


        try {
            performCleaning()
        } catch (e: Throwable) {
            Timber.e(e, "Error while clearing cache")
        }

        return Result.success()
    }

    private suspend fun performCleaning() {
        if (inputData.getBoolean(CLEAN_EVERYTHING, false)) {
            cleanAll(applicationContext)
        } else {
            cleanLRU(applicationContext)
        }
    }

    private suspend fun cleanLRU(context: Context) {
        val size = settingsManager().cacheSizeBytes().toLong()
        CacheCleaner.clean(context, size)
    }

    private suspend fun cleanAll(context: Context) {
        return CacheCleaner.cleanAll(context)
    }

    companion object {
        private val UNIQUE_WORK_ID: String = CacheCleanerWork::class.java.simpleName

        private const val CLEAN_EVERYTHING: String = "CLEAN_EVERYTHING"

        fun enqueueCleanCacheLRU(applicationContext: Context) {
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                UNIQUE_WORK_ID,
                ExistingWorkPolicy.APPEND,
                OneTimeWorkRequestBuilder<CacheCleanerWork>().build()
            )
        }

        fun cancelCleanCacheLRU(applicationContext: Context) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(UNIQUE_WORK_ID)
        }

        fun enqueueCleanCacheAll(applicationContext: Context) {
            val inputData: Data = workDataOf(CLEAN_EVERYTHING to true)

            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                UNIQUE_WORK_ID,
                ExistingWorkPolicy.APPEND,
                OneTimeWorkRequestBuilder<CacheCleanerWork>()
                    .setInputData(inputData)
                    .build()
            )
        }
    }
}
