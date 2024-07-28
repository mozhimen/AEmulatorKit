package com.mozhimen.emulatork.ui.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.common.archive.ArchiveManager
import com.mozhimen.emulatork.core.type.ECoreType
import com.mozhimen.emulatork.core.utils.CoreUtil
import com.mozhimen.emulatork.ext.library.NotificationsManager
import com.mozhimen.emulatork.ext.works.WorkScheduler
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

/**
 * @ClassName SaveSyncWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
abstract class AbsWorkSaveSync constructor(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams),IUtilK{

    //    @Inject
//    lateinit var saveSyncManager: SaveSyncManager
    abstract fun archiveManager(): ArchiveManager

    //    @Inject
//    lateinit var settingsManager: SettingsManager
    abstract fun settingManager(): SettingManager

    abstract fun gameActivityClazz(): Class<*>

    override suspend fun doWork(): Result {

        if (!shouldPerformSaveSync()) {
            return Result.success()
        }

        displayNotification()

        val coresToSync: Set<ECoreType> = settingManager().syncStatesCores()
            .mapNotNull { CoreUtil.findCoreTypeByCoreName(it) }
            .toSet()

        try {
            archiveManager().sync(coresToSync)
        } catch (e: Throwable) {
            com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.e(TAG,"Error in saves sync",e)
        }

        return Result.success()
    }

    private suspend fun shouldPerformSaveSync(): Boolean {
        val conditionsToRunThisWork = flow {
            emit(archiveManager().isSupported())
            emit(archiveManager().isConfigured())
            emit(settingManager().syncSaves())
            emit(shouldScheduleThisSync())
        }

        return conditionsToRunThisWork.firstOrNull { !it } ?: true
    }

    private suspend fun shouldScheduleThisSync(): Boolean {
        val isAutoSync = inputData.getBoolean(WorkScheduler.SAVE_SYNC_IS_AUTO, false)
        val isManualSync = !isAutoSync
        return settingManager().autoSaveSync() && isAutoSync || isManualSync
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
