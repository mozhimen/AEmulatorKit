package com.mozhimen.emulatork.core.utils

import com.mozhimen.emulatork.core.ECoreType
import com.mozhimen.emulatork.core.source.CoreSource
import com.mozhimen.emulatork.core.source.CoreSourceNo
import com.mozhimen.emulatork.core.source.CoreSourcePpsspp

/**
 * @ClassName CoreUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/29
 * @Version 1.0
 */
fun ECoreType.getCoreSource(): CoreSource =
    when (this) {
        ECoreType.PPSSPP -> CoreSourcePpsspp()
        else -> CoreSourceNo()
    }

object CoreUtil{
    @JvmStatic
    fun findCoreTypeByCoreName(coreName: String): ECoreType? =
        ECoreType.entries.firstOrNull { it.coreName == coreName }
}
