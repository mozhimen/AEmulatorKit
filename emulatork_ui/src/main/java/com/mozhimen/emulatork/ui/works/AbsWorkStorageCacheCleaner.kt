package com.mozhimen.emulatork.ui.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.ext.works.WorkScheduler
import timber.log.Timber
import com.mozhimen.emulatork.basic.cache.CacheCleaner
/**
 * @ClassName CacheCleanerWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsWorkStorageCacheCleaner(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    //    @Inject
//    lateinit var settingsManager: SettingsManager
    abstract fun settingManager(): SettingManager

    override suspend fun doWork(): Result {


        try {
            performCleaning()
        } catch (e: Throwable) {
            Timber.e(e, "Error while clearing cache")
        }

        return Result.success()
    }

    private suspend fun performCleaning() {
        if (inputData.getBoolean(WorkScheduler.STORAGE_CACHE_CLEANER_CLEAN_EVERYTHING, false)) {
            cleanAll(applicationContext)
        } else {
            cleanLRU(applicationContext)
        }
    }

    private suspend fun cleanLRU(context: Context) {
        val size = settingManager().cacheSizeBytes().toLong()
        CacheCleaner.clean(context, size)
    }

    private suspend fun cleanAll(context: Context) {
        return CacheCleaner.cleanAll(context)
    }
}
