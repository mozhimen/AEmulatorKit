package com.mozhimen.emulatork.core.option

import com.mozhimen.emulatork.core.property.CoreProperty
import java.io.Serializable

/**
 * @ClassName CoreOption
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
data class CoreOption(
    val coreProperty: CoreProperty,
    val name: String,
    val optionValues: List<String>
) : Serializable
