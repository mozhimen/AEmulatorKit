package com.mozhimen.emulatork.ui.dagger.works

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.core.CoreDownloader
import com.mozhimen.emulatork.basic.core.CoreSelection
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.common.dagger.AndroidWorkerInjection
import com.mozhimen.emulatork.common.dagger.annors.WorkerKey
import com.mozhimen.emulatork.ui.dagger.game.GameActivity
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
class WorkCoreUpdate(context: Context, workerParams: WorkerParameters) : AbsWorkCoreUpdate(context, workerParams) {
    @Inject
    lateinit var retrogradeDatabase: RetrogradeDatabase

    @Inject
    lateinit var coreDownloader: com.mozhimen.emulatork.core.CoreDownloader

    @Inject
    lateinit var coresSelection: CoreSelection


    override fun coreUpdater(): com.mozhimen.emulatork.core.CoreDownloader {
        return coreDownloader
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

    override suspend fun doWork(): Result {
        AndroidWorkerInjection.inject(this)
        return super.doWork()
    }

    @dagger.Module(subcomponents = [Subcomponent::class])
    abstract class Module {
        @Binds
        @IntoMap
        @WorkerKey(WorkCoreUpdate::class)
        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
    }

    @dagger.Subcomponent
    interface Subcomponent : AndroidInjector<WorkCoreUpdate> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<WorkCoreUpdate>()
    }
}