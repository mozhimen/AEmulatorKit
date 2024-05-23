package com.mozhimen.emulatork.ui.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.EmulatorKBasic
import com.mozhimen.emulatork.ext.library.NotificationsManager
import com.mozhimen.emulatork.ext.works.WorkScheduler
import com.mozhimen.emulatork.ui.game.AbsGameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @ClassName LibraryIndexWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
abstract class AbsWorkLibraryIndex(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    abstract fun lemuroidLibrary(): EmulatorKBasic

    abstract fun gameActivityClazz(): Class<out AbsGameActivity>

    abstract fun workCoreUpdateClazz(): Class<out AbsWorkCoreUpdate>

    override suspend fun doWork(): Result {
        val notificationsManager = NotificationsManager(applicationContext, gameActivityClazz())

        val foregroundInfo = ForegroundInfo(
            NotificationsManager.LIBRARY_INDEXING_NOTIFICATION_ID,
            notificationsManager.libraryIndexingNotification()
        )

        setForegroundAsync(foregroundInfo)

        val result = withContext(Dispatchers.IO) {
            kotlin.runCatching {
                lemuroidLibrary().indexLibrary()
            }
        }

        result.exceptionOrNull()?.let {
            Timber.e("Library indexing work terminated with an exception:", it)
        }

        WorkScheduler.scheduleCoreUpdate(workCoreUpdateClazz(), applicationContext)

        return Result.success()
    }
}
