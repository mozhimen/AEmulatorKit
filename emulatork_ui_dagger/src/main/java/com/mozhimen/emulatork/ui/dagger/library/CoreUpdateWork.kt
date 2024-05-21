package com.mozhimen.emulatork.ui.dagger.library

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.library.CoreUpdateWork
import com.mozhimen.emulatork.basic.dagger.AndroidWorkerInjection
import com.mozhimen.emulatork.basic.dagger.interfaces.WorkerKey
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Inject

/**
 * @ClassName CoreUpdateWork
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:21
 * @Version 1.0
 */
class CoreUpdateWork(context: Context, workerParams: WorkerParameters) : CoreUpdateWork(context, workerParams) {
    @Inject
    lateinit var retrogradeDatabase: RetrogradeDatabase

    @Inject
    lateinit var coreUpdater: CoreUpdater

    @Inject
    lateinit var coresSelection: CoresSelection

    override fun getCoreUpdater(): CoreUpdater {
        return coreUpdater
    }

    override fun getCoresSelection(): CoresSelection {
        return coresSelection
    }

    override fun getRetrogradeDatabase(): RetrogradeDatabase {
        return retrogradeDatabase
    }

    override suspend fun doWork(): Result {
        AndroidWorkerInjection.inject(this)
        return super.doWork()
    }

    @dagger.Module(subcomponents = [Subcomponent::class])
    abstract class Module {
        @Binds
        @IntoMap
        @WorkerKey(CoreUpdateWork::class)
        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
    }

    @dagger.Subcomponent
    interface Subcomponent : AndroidInjector<CoreUpdateWork> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<CoreUpdateWork>()
    }
}