package com.mozhimen.emulatork.ui.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.common.core.CoreSelectionManager
import com.mozhimen.emulatork.core.download.CoreDownload
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.ext.library.NotificationsManager
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import timber.log.Timber
import com.mozhimen.emulatork.common.system.SystemProvider
/**
 * @ClassName CoreUpdateWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
abstract class AbsWorkCoreUpdate(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    //    @Inject
//    lateinit var retrogradeDatabase: RetrogradeDatabase
    abstract fun retrogradeDatabase(): RetrogradeDatabase

    //    @Inject
//    lateinit var coreUpdater: CoreUpdater
    abstract fun coreDownload(): CoreDownload

    //    @Inject
//    lateinit var coresSelection: CoresSelection
    abstract fun coreSelectionManager(): CoreSelectionManager

    abstract fun gameActivityClazz(): Class<*>

    override suspend fun doWork(): Result {

        Timber.i("Starting core update/install work")

        val notificationsManager = NotificationsManager(applicationContext, gameActivityClazz())

        val foregroundInfo = ForegroundInfo(
            NotificationsManager.CORE_INSTALL_NOTIFICATION_ID,
            notificationsManager.installingCoresNotification()
        )

        setForegroundAsync(foregroundInfo)

        try {
            val cores = retrogradeDatabase().gameDao().selectSystems()
                .asFlow()
                .map { SystemProvider.findSysByName(it) }
                .map { coreSelectionManager().getCoreConfigForSystem(it) }
                .map { it.eCoreType }
                .toList()

            coreDownload().downloadCores(applicationContext, cores)
        } catch (e: Throwable) {
            Timber.e(e, "Core update work failed with exception: ${e.message}")
        }

        return Result.success()
    }
}

