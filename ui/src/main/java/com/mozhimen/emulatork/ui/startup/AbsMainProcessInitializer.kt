package com.mozhimen.emulatork.ui.startup

import android.content.Context
import androidx.startup.Initializer
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.startup.DebugInitializer
import com.mozhimen.emulatork.ext.works.WorkScheduler
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync


/**
 * @ClassName MainProcessInitializer
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
abstract class AbsMainProcessInitializer : Initializer<Unit>, IUtilK {
    abstract fun workSaveSyncClazz(): Class<out AbsWorkSaveSync>
    abstract fun workCoreUpdateClazz(): Class<out AbsWorkCoreUpdate>
    override fun create(context: Context) {
         com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.i(TAG,"Requested initialization of main process tasks")
            WorkScheduler.enqueueAutoWork(workSaveSyncClazz(), context, 0)
            WorkScheduler.scheduleCoreUpdate(workCoreUpdateClazz(), context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(/*WorkManagerInitializer::class.java,*/ DebugInitializer::class.java)
    }
}

