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
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.ui.works.AbsWorkLibraryIndex
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.ui.works.AbsWorkStorageCacheCleaner
import java.util.concurrent.TimeUnit

/**
 * @ClassName LibraryIndexScheduler
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object WorkScheduler {
    val WORK_ID_CORE_UPDATE: String = AbsWorkCoreUpdate::class.java.simpleName
    val WORK_ID_LIBRARY_INDEX: String = AbsWorkLibraryIndex::class.java.simpleName
    val WORK_ID_SAVE_SYNC: String = AbsWorkSaveSync::class.java.simpleName
    val WORK_ID_SAVE_SYNC_PERIODIC: String = AbsWorkSaveSync::class.java.simpleName + "Periodic"
    val WORK_ID_STORAGE_CACHE_CLEANER: String = AbsWorkStorageCacheCleaner::class.java.simpleName
    const val SAVE_SYNC_IS_AUTO = "IS_AUTO"
    const val STORAGE_CACHE_CLEANER_CLEAN_EVERYTHING = "CLEAN_EVERYTHING"

    fun scheduleLibrarySync(workLibraryIndexClazz: Class<out AbsWorkLibraryIndex>, applicationContext: Context) {
        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                WORK_ID_LIBRARY_INDEX,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                /*OneTimeWorkRequestBuilder<AbsWorkLibraryIndex>()*/OneTimeWorkRequest.Builder(workLibraryIndexClazz).build()
            )
            .enqueue()
    }

    fun cancelLibrarySync(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_LIBRARY_INDEX)
    }

    ////////////////////////////////////////////////////////////////////////////////////

    fun scheduleCoreUpdate(workCoreUpdateClazz: Class<out AbsWorkCoreUpdate>, applicationContext: Context) {
        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                WORK_ID_CORE_UPDATE,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                /*OneTimeWorkRequestBuilder<AbsWorkCoreUpdate>()*/OneTimeWorkRequest.Builder(workCoreUpdateClazz).build()
            )
            .enqueue()
    }

    fun cancelCoreUpdate(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_CORE_UPDATE)
    }

    ////////////////////////////////////////////////////////////////////////////////////

    fun enqueueManualWork(workSaveSyncClazz: Class<out AbsWorkSaveSync>, applicationContext: Context) {
        val inputData: Data = workDataOf(SAVE_SYNC_IS_AUTO to false)

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_ID_SAVE_SYNC,
            ExistingWorkPolicy.REPLACE,
            /* OneTimeWorkRequestBuilder<AbsWorkSaveSync>()*/OneTimeWorkRequest.Builder(workSaveSyncClazz)
                .setInputData(inputData)
                .build()
        )
    }

    fun cancelManualWork(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_SAVE_SYNC)
    }

    ////////////////////////////////////////////////////////////////////////////////////

    fun enqueueAutoWork(workSaveSyncClazz: Class<out AbsWorkSaveSync>, applicationContext: Context, delayMinutes: Long = 0) {
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

    fun cancelAutoWork(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_SAVE_SYNC_PERIODIC)
    }

    ////////////////////////////////////////////////////////////////////////////////////

    fun enqueueCleanCacheLRU(workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>, applicationContext: Context) {
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_ID_STORAGE_CACHE_CLEANER,
            ExistingWorkPolicy.APPEND,
            /*OneTimeWorkRequestBuilder<AbsWorkStorageCacheCleaner>()*/OneTimeWorkRequest.Builder(workStorageCacheCleanerClazz).build()
        )
    }

    fun cancelCleanCacheLRU(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_ID_STORAGE_CACHE_CLEANER)
    }

    ////////////////////////////////////////////////////////////////////////////////////

    fun enqueueCleanCacheAll(workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>, applicationContext: Context) {
        val inputData: Data = workDataOf(STORAGE_CACHE_CLEANER_CLEAN_EVERYTHING to true)

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_ID_STORAGE_CACHE_CLEANER,
            ExistingWorkPolicy.APPEND,
            /*OneTimeWorkRequestBuilder<AbsWorkStorageCacheCleaner>()*/OneTimeWorkRequest.Builder(workStorageCacheCleanerClazz)
                .setInputData(inputData)
                .build()
        )
    }
}
