package com.mozhimen.emulatork.test.hilt.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManager
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.basic.game.setting.GameSettingsManager
import com.mozhimen.emulatork.ui.hilt.game.GameActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * @ClassName SaveSyncWork
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 21:41
 * @Version 1.0
 */
@HiltWorker
class WorkSaveSync @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val saveSyncManager: SaveSyncManager,
    private val gameSettingsManager: GameSettingsManager,
) : AbsWorkSaveSync(context, workerParams) {

    override fun saveSyncManager(): SaveSyncManager {
        return saveSyncManager
    }

    override fun settingsManager(): GameSettingsManager {
        return gameSettingsManager
    }

    override fun gameActivityClazz(): Class<*> {
        return GameActivity::class.java
    }

//    @dagger.Module(subcomponents = [Subcomponent::class])
//    abstract class Module {
//        @Binds
//        @IntoMap
//        @WorkerKey(WorkSaveSync::class)
//        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
//    }
//
//    @dagger.Subcomponent
//    interface Subcomponent : AndroidInjector<WorkSaveSync> {
//        @dagger.Subcomponent.Builder
//        abstract class Builder : AndroidInjector.Builder<WorkSaveSync>()
//    }
}