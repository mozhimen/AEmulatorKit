package com.mozhimen.emulatork.ui.dagger.works

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.dagger.interfaces.WorkerKey
import com.mozhimen.emulatork.basic.library.LemuroidLibrary
import com.mozhimen.emulatork.ui.library.AbsLibraryIndexWork
import com.mozhimen.emulatork.basic.dagger.AndroidWorkerInjection
import com.mozhimen.emulatork.ui.dagger.game.GameActivity
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Inject

/**
 * @ClassName LibraryIndexWork
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:29
 * @Version 1.0
 */
class LibraryIndexWork(context: Context, workerParams: WorkerParameters):AbsLibraryIndexWork(context, workerParams) {

    @Inject
    lateinit var lemuroidLibrary: LemuroidLibrary

    override fun lemuroidLibrary(): LemuroidLibrary {
        return lemuroidLibrary
    }

    override fun gameActivityClazz(): Class<*> {
        return GameActivity::class.java
    }

    override suspend fun doWork(): Result {
        AndroidWorkerInjection.inject(this)
        return super.doWork()
    }
    @dagger.Module(subcomponents = [Subcomponent::class])
    abstract class Module {
        @Binds
        @IntoMap
        @WorkerKey(AbsLibraryIndexWork::class)
        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
    }

    @dagger.Subcomponent
    interface Subcomponent : AndroidInjector<AbsLibraryIndexWork> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<AbsLibraryIndexWork>()
    }
}