package com.mozhimen.emulatork.ui.dagger.library

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.dagger.interfaces.WorkerKey
import com.mozhimen.emulatork.basic.library.LemuroidLibrary
import com.mozhimen.emulatork.ui.library.LibraryIndexWork
import com.mozhimen.emulatork.basic.dagger.AndroidWorkerInjection
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
class LibraryIndexWork(context: Context, workerParams: WorkerParameters):LibraryIndexWork(context, workerParams) {

    @Inject
    lateinit var lemuroidLibrary: LemuroidLibrary

    override fun getLemuroidLibrary(): LemuroidLibrary {
        return lemuroidLibrary
    }

    override suspend fun doWork(): Result {
        AndroidWorkerInjection.inject(this)
        return super.doWork()
    }
    @dagger.Module(subcomponents = [Subcomponent::class])
    abstract class Module {
        @Binds
        @IntoMap
        @WorkerKey(LibraryIndexWork::class)
        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
    }

    @dagger.Subcomponent
    interface Subcomponent : AndroidInjector<LibraryIndexWork> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<LibraryIndexWork>()
    }
}