package com.mozhimen.emulatork.ext.works

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.ui.works.AbsWorkLibraryIndex
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.ui.works.AbsWorkStorageCacheCleaner
import com.squareup.moshi.internal.Util
import java.util.concurrent.TimeUnit

/**
 * @ClassName LibraryIndexScheduler
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object WorkScheduler : IUtilK {
    val WORK_ID_CORE_UPDATE: String = AbsWorkCoreUpdate::class.java.simpleName
    val WORK_ID_LIBRARY_INDEX: String = AbsWorkLibraryIndex::class.java.simpleName
    val WORK_ID_SAVE_SYNC: String = AbsWorkSaveSync::class.java.simpleName
    val WORK_ID_SAVE_SYNC_PERIODIC: String = AbsWorkSaveSync::class.java.simpleName + "Periodic"
    val WORK_ID_STORAGE_CACHE_CLEANER: String = AbsWorkStorageCacheCleaner::class.java.simpleName
    const val SAVE_SYNC_IS_AUTO = "IS_AUTO"
    const val STORAGE_CACHE_CLEANER_CLEAN_EVERYTHING = "CLEAN_EVERYTHING"

    ////////////////////////////////////////////////////////////////////////////////////

    fun scheduleLibrarySync(tag: String, workLibraryIndexClazz: Class<out AbsWorkLibraryIndex>, applicationContext: Context) {
        UtilKLogWrapper.w(TAG, "scheduleLibrarySync $tag")

        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                WORK_ID_LIBRARY_INDEX,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                /*OneTimeWorkRequestBuilder<AbsWorkLibraryIndex>()*/OneTimeWorkRequest.Builder(workLibraryIndexClazz).build()
            )
            .enqueue()
    }

    fun scheduleCoreUpdate(tag: String, workCoreUpdateClazz: Class<out AbsWorkCoreUpdate>, applicationContext: Context) {
        UtilKLogWrapper.w(TAG, "scheduleCoreUpdate $tag")

        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                WORK_ID_CORE_UPDATE,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                /*OneTimeWorkRequestBuilder<AbsWorkCoreUpdate>()*/OneTimeWorkRequest.Builder(workCoreUpdateClazz).build()
            )
            .enqueue()
    }

    fun enqueueManualWork(tag: String, workSaveSyncClazz: Class<out AbsWorkSaveSync>, applicationContext: Context) {
        UtilKLogWrapper.w(TAG, "enqueueManualWork $tag")

        val inputData: Data = workDataOf(SAVE_SYNC_IS_AUTO to false)

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_ID_SAVE_SYNC,
            ExistingWorkPolicy.REPLACE,
            /* OneTimeWorkRequestBuilder<AbsWorkSaveSync>()*/OneTimeWorkRequest.Builder(workSaveSyncClazz)
                .setInputData(inputData)
                .build()
        )
    }

    fun enqueueAutoWork(tag: String, workSaveSyncClazz: Class<out AbsWorkSaveSync>, applicationContext: Context, delayMinutes: Long = 0) {
        UtilKLogWrapper.w(TAG, "enqueueAutoWork $tag")

        val inputData: Data = workDataOf(SAVE_SYNC_IS_AUTO to true)

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            WORK_ID_SAVE_SYNC_PERIODIC,
            ExistingPeriodicWorkPolicy.REPLACE,
            /*PeriodicWorkRequestBuilder<AbsWorkSaveSync>(3, TimeUnit.HOURS)*/PeriodicWorkRequest.Builder(workSaveSyncClazz, 3, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .setInputData(inputData)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .build()
        )
    }

    fun enqueueCleanCacheLRU(tag: String, workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>, applicationContext: Context) {
        UtilKLogWrapper.w(TAG, "enqueueCleanCacheLRU $tag")

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_ID_STORAGE_CACHE_CLEANER,
            ExistingWorkPolicy.APPEND,
            /*OneTimeWorkRequestBuilder<AbsWorkStorageCacheCleaner>()*/OneTimeWorkRequest.Builder(workStorageCacheCleanerClazz).build()
        )
    }

    fun enqueueCleanCacheAll(tag: String, workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>, applicationContext: Context) {
        UtilKLogWrapper.w(TAG, "enqueueCleanCacheAll $tag")

        val inputData: Data = workDataOf(STORAGE_CACHE_CLEANER_CLEAN_EVERYTHING to true)

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_ID_STORAGE_CACHE_CLEANER,
            ExistingWorkPolicy.APPEND,
            /*OneTimeWorkRequestBuilder<AbsWorkStorageCacheCleaner>()*/OneTimeWorkRequest.Builder(workStorageCacheCleanerClazz)
                .setInputData(inputData)
                .build()
        )
    }

    ////////////////////////////////////////////////////////////////////////////////////

    fun cancelLibrarySync(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_LIBRARY_INDEX)
    }

    fun cancelCoreUpdate(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_CORE_UPDATE)
    }

    fun cancelManualWork(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_SAVE_SYNC)
    }

    fun cancelAutoWork(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_SAVE_SYNC_PERIODIC)
    }

    fun cancelCleanCacheLRU(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_STORAGE_CACHE_CLEANER)
    }
}
