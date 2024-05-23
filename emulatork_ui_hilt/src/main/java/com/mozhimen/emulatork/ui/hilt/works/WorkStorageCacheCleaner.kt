package com.mozhimen.emulatork.ui.hilt.works

import android.content.Context
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.game.setting.GameSettingsManager
import com.mozhimen.emulatork.ui.works.AbsWorkStorageCacheCleaner
import javax.inject.Inject

/**
 * @ClassName CacheCleanerWork
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class WorkStorageCacheCleaner(
    context: Context,
    workerParams: WorkerParameters
) : AbsWorkStorageCacheCleaner(context, workerParams) {

    @Inject
    lateinit var gameSettingsManager: GameSettingsManager

    override fun settingsManager(): GameSettingsManager {
        return gameSettingsManager
    }

//    @dagger.Module(subcomponents = [Subcomponent::class])
//    abstract class Module {
//        @Binds
//        @IntoMap
//        @WorkerKey(WorkStorageCacheCleaner::class)
//        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
//    }
//
//    @dagger.Subcomponent
//    interface Subcomponent : AndroidInjector<WorkStorageCacheCleaner> {
//        @dagger.Subcomponent.Builder
//        abstract class Builder : AndroidInjector.Builder<WorkStorageCacheCleaner>()
//    }
}