package com.mozhimen.emulatork.ui.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.core.findByName
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManager
import com.mozhimen.emulatork.ext.library.NotificationsManager
import com.mozhimen.emulatork.basic.game.setting.GameSettingsManager
import com.mozhimen.emulatork.ext.works.WorkScheduler
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * @ClassName SaveSyncWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
abstract class AbsWorkSaveSync constructor(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    //    @Inject
//    lateinit var saveSyncManager: SaveSyncManager
    abstract fun saveSyncManager(): SaveSyncManager

    //    @Inject
//    lateinit var settingsManager: SettingsManager
    abstract fun settingsManager(): GameSettingsManager

    abstract fun gameActivityClazz(): Class<*>

    override suspend fun doWork(): Result {

        if (!shouldPerformSaveSync()) {
            return Result.success()
        }

        displayNotification()

        val coresToSync = settingsManager().syncStatesCores()
            .mapNotNull { com.mozhimen.emulatork.core.findByName(it) }
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
        val isAutoSync = inputData.getBoolean(WorkScheduler.SAVE_SYNC_IS_AUTO, false)
        val isManualSync = !isAutoSync
        return settingsManager().autoSaveSync() && isAutoSync || isManualSync
    }

    private fun displayNotification() {
        val notificationsManager = NotificationsManager(applicationContext, gameActivityClazz())

        val foregroundInfo = ForegroundInfo(
            NotificationsManager.SAVE_SYNC_NOTIFICATION_ID,
            notificationsManager.saveSyncNotification()
        )
        setForegroundAsync(foregroundInfo)
    }
}
