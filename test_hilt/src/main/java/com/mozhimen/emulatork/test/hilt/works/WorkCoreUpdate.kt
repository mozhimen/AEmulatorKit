package com.mozhimen.emulatork.test.hilt.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.core.CoreSelection
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.test.hilt.game.GameActivity
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
    private val coreUpdater: CoreUpdater,
    private val coresSelection: CoreSelection
) : AbsWorkCoreUpdate(context, workerParams) {

    override fun coreUpdater(): CoreUpdater {
        return coreUpdater
    }

    override fun coresSelection(): CoreSelection {
        return coresSelection
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