package com.mozhimen.emulatork.core.utils

import com.mozhimen.emulatork.core.ECoreType

/**
 * @ClassName CoreUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/29
 * @Version 1.0
 */
object CoreUtil{
    @JvmStatic
    fun findCoreTypeByCoreName(coreName: String): ECoreType? =
        ECoreType.entries.firstOrNull { it.coreName == coreName }
}
