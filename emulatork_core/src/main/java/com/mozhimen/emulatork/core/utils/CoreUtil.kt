package com.mozhimen.emulatork.core.utils

import com.mozhimen.emulatork.core.ECoreType
import com.mozhimen.emulatork.core.option.CoreOption
import com.mozhimen.emulatork.core.property.CoreProperty
import com.mozhimen.emulatork.core.source.CoreSource
import com.mozhimen.emulatork.core.source.CoreSourceNo
import com.mozhimen.emulatork.core.source.CoreSourcePpsspp
import com.swordfish.libretrodroid.Variable

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

fun Variable.toCoreOption(): CoreOption {
    val name = this.description?.split(";")?.get(0)!!
    val values = this.description?.split(";")?.get(1)?.trim()?.split('|') ?: listOf()
    val coreProperty = CoreProperty(this.key!!, this.value!!)
    return CoreOption(coreProperty, name, values)
}

object CoreUtil {
    @JvmStatic
    fun findCoreTypeByCoreName(coreName: String): ECoreType? =
        ECoreType.entries.firstOrNull { it.coreName == coreName }
}
