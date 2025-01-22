package com.mozhimen.emulatork.ui.hilt.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.common.core.CoreSelectionManager
import com.mozhimen.emulatork.core.download.CoreDownload
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.ui.hilt.game.GameActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * @ClassName CoreUpdateWork
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:21
 * @Version 1.0
 */
@HiltWorker
class WorkCoreUpdate @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val retrogradeDatabase: RetrogradeDatabase,
    private val coreDownload: CoreDownload,
    private val coreSelectionManager: CoreSelectionManager
) : AbsWorkCoreUpdate(context, workerParams) {

    override fun coreDownload(): CoreDownload {
        return coreDownload
    }

    override fun coreSelectionManager(): CoreSelectionManager {
        return coreSelectionManager
    }

    override fun gameActivityClazz(): Class<*> {
        return GameActivity::class.java
    }

    override fun retrogradeDatabase(): RetrogradeDatabase {
        return retrogradeDatabase
    }

//    @dagger.Module(subcomponents = [Subcomponent::class])
//    abstract class Module {
//        @Binds
//        @IntoMap
//        @WorkerKey(WorkCoreUpdate::class)
//        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
//    }
//
//    @dagger.Subcomponent
//    interface Subcomponent : AndroidInjector<WorkCoreUpdate> {
//        @dagger.Subcomponent.Builder
//        abstract class Builder : AndroidInjector.Builder<WorkCoreUpdate>()
//    }
}