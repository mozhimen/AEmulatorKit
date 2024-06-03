package com.mozhimen.emulatork.basic.system

import java.io.Serializable

/**
 * @ClassName ExposedSetting
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class SystemSetting(
    val key: String,
    val titleId: Int,
    val values: ArrayList<SystemProperty> = arrayListOf(),
) : Serializable
