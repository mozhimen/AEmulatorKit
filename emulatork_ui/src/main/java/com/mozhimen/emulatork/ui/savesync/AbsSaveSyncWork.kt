package com.mozhimen.emulatork.ui.savesync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mozhimen.emulatork.basic.library.findByName
import com.mozhimen.emulatork.basic.savesync.SaveSyncManager
import com.mozhimen.emulatork.ui.main.NotificationsManager
import com.mozhimen.emulatork.ui.settings.SettingsManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * @ClassName SaveSyncWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
abstract class AbsSaveSyncWork(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

//    @Inject
//    lateinit var saveSyncManager: SaveSyncManager
      abstract  fun saveSyncManager():SaveSyncManager

//    @Inject
//    lateinit var settingsManager: SettingsManager
      abstract fun settingsManager(): SettingsManager

    abstract fun gameActivityClazz(): Class<*>

    override suspend fun doWork(): Result {

        if (!shouldPerformSaveSync()) {
            return Result.success()
        }

        displayNotification()

        val coresToSync = settingsManager().syncStatesCores()
            .mapNotNull { findByName(it) }
            .toSet()

        try {
            saveSyncManager().sync(coresToSync)
        } catch (e: Throwable) {
            Timber.e(e, "Error in saves sync")
        }

        return Result.success()
    }

    private suspend fun shouldPerformSaveSync(): Boolean {
        val conditionsToRunThisWork = flow {
            emit(saveSyncManager().isSupported())
            emit(saveSyncManager().isConfigured())
            emit(settingsManager().syncSaves())
            emit(shouldScheduleThisSync())
        }

        return conditionsToRunThisWork.firstOrNull { !it } ?: true
    }

    private suspend fun shouldScheduleThisSync(): Boolean {
        val isAutoSync = inputData.getBoolean(IS_AUTO, false)
        val isManualSync = !isAutoSync
        return settingsManager().autoSaveSync() && isAutoSync || isManualSync
    }

    private fun displayNotification() {
        val notificationsManager = NotificationsManager(applicationContext,gameActivityClazz())

        val foregroundInfo = ForegroundInfo(
            NotificationsManager.SAVE_SYNC_NOTIFICATION_ID,
            notificationsManager.saveSyncNotification()
        )
        setForegroundAsync(foregroundInfo)
    }

    companion object {
        val UNIQUE_WORK_ID: String = AbsSaveSyncWork::class.java.simpleName
        val UNIQUE_PERIODIC_WORK_ID: String = AbsSaveSyncWork::class.java.simpleName + "Periodic"
        private const val IS_AUTO = "IS_AUTO"

        fun enqueueManualWork(applicationContext: Context) {
            val inputData: Data = workDataOf(IS_AUTO to false)

            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                UNIQUE_WORK_ID,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<AbsSaveSyncWork>()
                    .setInputData(inputData)
                    .build()
            )
        }

        fun enqueueAutoWork(applicationContext: Context, delayMinutes: Long = 0) {
            val inputData: Data = workDataOf(IS_AUTO to true)

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC_WORK_ID,
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<AbsSaveSyncWork>(3, TimeUnit.HOURS)
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

        fun cancelManualWork(applicationContext: Context) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(UNIQUE_WORK_ID)
        }

        fun cancelAutoWork(applicationContext: Context) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(UNIQUE_PERIODIC_WORK_ID)
        }
    }


}
