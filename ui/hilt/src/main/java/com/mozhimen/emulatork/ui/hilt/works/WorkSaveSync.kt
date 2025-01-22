package com.mozhimen.emulatork.ui.hilt.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.common.archive.ArchiveManager
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
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
    private val archiveManager: ArchiveManager,
    private val settingManager: SettingManager,
) : AbsWorkSaveSync(context, workerParams) {

    override fun archiveManager(): ArchiveManager {
        return archiveManager
    }

    override fun settingManager(): SettingManager {
        return settingManager
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