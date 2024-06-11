package com.mozhimen.emulatork.basic.system

import android.content.Context

/**
 * @ClassName SystemMetaInfo
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
data class SystemMetadata constructor(val eSystemMetaType: ESystemMetaType, val count: Int) {
    fun getName(context: Context): String =
        context.resources.getString(eSystemMetaType.titleResId)
}
