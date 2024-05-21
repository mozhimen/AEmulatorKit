package com.mozhimen.emulatork.ui.library

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.main.NotificationsManager
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import timber.log.Timber

/**
 * @ClassName CoreUpdateWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
abstract class AbsCoreUpdateWork(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    //    @Inject
//    lateinit var retrogradeDatabase: RetrogradeDatabase
    abstract fun retrogradeDatabase(): RetrogradeDatabase

    //    @Inject
//    lateinit var coreUpdater: CoreUpdater
    abstract fun coreUpdater(): CoreUpdater

    //    @Inject
//    lateinit var coresSelection: CoresSelection
    abstract fun coresSelection(): CoresSelection

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
                .map { GameSystem.findById(it) }
                .map { coresSelection().getCoreConfigForSystem(it) }
                .map { it.coreID }
                .toList()

            coreUpdater().downloadCores(applicationContext, cores)
        } catch (e: Throwable) {
            Timber.e(e, "Core update work failed with exception: ${e.message}")
        }

        return Result.success()
    }


}

