package com.mozhimen.emulatork.ui.dagger.startup

import androidx.startup.Initializer
import com.mozhimen.emulatork.basic.startup.DebugInitializer
import com.mozhimen.emulatork.ui.startup.AbsMainProcessInitializer
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.ui.dagger.works.WorkSaveSync
import com.mozhimen.emulatork.ui.dagger.works.WorkCoreUpdate

/**
 * @ClassName MainProcessInitializer
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/23
 * @Version 1.0
 */
class MainProcessInitializer : AbsMainProcessInitializer() {
    override fun workSaveSyncClazz(): Class<out AbsWorkSaveSync> {
        return WorkSaveSync::class.java
    }

    override fun workCoreUpdateClazz(): Class<out AbsWorkCoreUpdate> {
        return WorkCoreUpdate::class.java
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(androidx.work.WorkManagerInitializer::class.java, DebugInitializer::class.java)
    }
}