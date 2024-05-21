package com.mozhimen.emulatork.ui.startup

import android.content.Context
import androidx.startup.Initializer
import androidx.work.WorkManagerInitializer
import timber.log.Timber

/**
 * @ClassName MainProcessInitializer
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class MainProcessInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.i("Requested initialization of main process tasks")
        com.mozhimen.emulatork.ui.savesync.AbsSaveSyncWork.enqueueAutoWork(context, 0)
        com.mozhimen.emulatork.ui.library.LibraryIndexScheduler.scheduleCoreUpdate(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(WorkManagerInitializer::class.java, DebugInitializer::class.java)
    }
}

