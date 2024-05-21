package com.mozhimen.emulatork.ui.hilt.works

import android.content.Context
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.ui.settings.SettingsManager
import com.mozhimen.emulatork.ui.storage.cache.CacheCleanerWork
import javax.inject.Inject

/**
 * @ClassName CacheCleanerWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class CacheCleanerWork(
    context: Context,
    workerParams: WorkerParameters
) : CacheCleanerWork(context, workerParams) {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun settingsManager(): SettingsManager {
        return settingsManager
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
//        @WorkerKey(CacheCleanerWork::class)
//        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
//    }
//
//    @dagger.Subcomponent
//    interface Subcomponent : AndroidInjector<CacheCleanerWork> {
//        @dagger.Subcomponent.Builder
//        abstract class Builder : AndroidInjector.Builder<CacheCleanerWork>()
//    }
}