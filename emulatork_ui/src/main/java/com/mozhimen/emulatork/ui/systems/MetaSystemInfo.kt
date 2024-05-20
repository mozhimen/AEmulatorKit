package com.mozhimen.emulatork.ui.systems

import android.content.Context
import com.mozhimen.emulatork.basic.library.MetaSystemID

/**
 * @ClassName MetaSystemInfo
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
data class MetaSystemInfo(val metaSystem: MetaSystemID, val count: Int) {
    fun getName(context: Context) = context.resources.getString(metaSystem.titleResId)
}
