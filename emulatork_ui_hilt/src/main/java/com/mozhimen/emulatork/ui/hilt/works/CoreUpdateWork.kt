package com.mozhimen.emulatork.ui.hilt.works

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.library.AbsCoreUpdateWork
import com.mozhimen.emulatork.ui.hilt.game.GameActivity
import javax.inject.Inject

/**
 * @ClassName CoreUpdateWork
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:21
 * @Version 1.0
 */
class CoreUpdateWork(context: Context, workerParams: WorkerParameters) : AbsCoreUpdateWork(context, workerParams) {
    @Inject
    lateinit var retrogradeDatabase: RetrogradeDatabase

    @Inject
    lateinit var coreUpdater: CoreUpdater

    @Inject
    lateinit var coresSelection: CoresSelection


    override fun coreUpdater(): CoreUpdater {
        return coreUpdater
    }

    override fun coresSelection(): CoresSelection {
        return coresSelection
    }

    override fun gameActivityClazz(): Class<*> {
        return GameActivity::class.java
    }

    override fun retrogradeDatabase(): RetrogradeDatabase {
        return retrogradeDatabase
    }

//    override suspend fun doWork(): Result {
//        AndroidWorkerInjection.inject(this)
//        return super.doWork()
//    }
//
//    @dagger.Module(subcomponents = [Subcomponent::class])
//    abstract class Module {
//        @Binds
//        @IntoMap
//        @WorkerKey(AbsCoreUpdateWork::class)
//        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
//    }
//
//    @dagger.Subcomponent
//    interface Subcomponent : AndroidInjector<AbsCoreUpdateWork> {
//        @dagger.Subcomponent.Builder
//        abstract class Builder : AndroidInjector.Builder<AbsCoreUpdateWork>()
//    }
}