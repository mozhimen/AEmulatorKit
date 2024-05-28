package com.mozhimen.emulatork.ui.hilt.startup

import com.mozhimen.emulatork.ui.startup.AbsMainProcessInitializer
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.test.hilt.works.WorkSaveSync
import com.mozhimen.emulatork.test.hilt.works.WorkCoreUpdate

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
}