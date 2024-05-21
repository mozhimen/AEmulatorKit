package com.mozhimen.emulatork.ui.library

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.library.LemuroidLibrary
import com.mozhimen.emulatork.ui.main.NotificationsManager
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
abstract class AbsLibraryIndexWork(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

//    @Inject
//    lateinit var lemuroidLibrary: LemuroidLibrary
    abstract fun lemuroidLibrary(): LemuroidLibrary

    abstract fun gameActivityClazz(): Class<*>

    override suspend fun doWork(): Result {
//        AndroidWorkerInjection.inject(this)

        val notificationsManager = NotificationsManager(applicationContext,gameActivityClazz())

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

        LibraryIndexScheduler.scheduleCoreUpdate(applicationContext)

        return Result.success()
    }


}
