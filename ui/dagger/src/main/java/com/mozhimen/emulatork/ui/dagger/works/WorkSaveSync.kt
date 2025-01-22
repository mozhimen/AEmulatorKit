package com.mozhimen.emulatork.ui.dagger.works

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.common.archive.ArchiveManager
import com.mozhimen.emulatork.common.dagger.annors.WorkerKey
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.common.dagger.AndroidWorkerInjection
import com.mozhimen.emulatork.ui.dagger.game.GameActivity
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Inject

/**
 * @ClassName SaveSyncWork
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 21:41
 * @Version 1.0
 */
class WorkSaveSync constructor(context: Context, workerParams: WorkerParameters) : AbsWorkSaveSync(context, workerParams) {

    @Inject
    lateinit var saveSyncManager: ArchiveManager

    @Inject
    lateinit var settingsManager: SettingManager

    override fun archiveManager(): ArchiveManager {
        return saveSyncManager
    }

    override fun settingManager(): SettingManager {
        return settingsManager
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
        @WorkerKey(WorkSaveSync::class)
        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
    }

    @dagger.Subcomponent
    interface Subcomponent : AndroidInjector<WorkSaveSync> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<WorkSaveSync>()
    }
}