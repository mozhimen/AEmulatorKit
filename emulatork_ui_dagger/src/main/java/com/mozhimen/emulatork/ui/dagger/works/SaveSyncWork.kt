package com.mozhimen.emulatork.ui.dagger.works

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.dagger.interfaces.WorkerKey
import com.mozhimen.emulatork.basic.savesync.SaveSyncManager
import com.mozhimen.emulatork.ui.savesync.AbsSaveSyncWork
import com.mozhimen.emulatork.ui.settings.SettingsManager
import com.mozhimen.emulatork.basic.dagger.AndroidWorkerInjection
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
class SaveSyncWork(context: Context, workerParams: WorkerParameters) : AbsSaveSyncWork(context, workerParams) {

    @Inject
    lateinit var saveSyncManager: SaveSyncManager

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun saveSyncManager(): SaveSyncManager {
        return saveSyncManager
    }

    override fun settingsManager(): SettingsManager {
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
        @WorkerKey(AbsSaveSyncWork::class)
        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
    }

    @dagger.Subcomponent
    interface Subcomponent : AndroidInjector<AbsSaveSyncWork> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<AbsSaveSyncWork>()
    }
}