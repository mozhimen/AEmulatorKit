package com.mozhimen.emulatork.common.game.system

import java.io.Serializable

/**
 * @ClassName ExposedSetting
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class GameSystemExposedSetting(
    val key: String,
    val titleId: Int,
    val values: ArrayList<Value> = arrayListOf(),
) : Serializable {
    data class Value(val key: String, val titleId: Int) : Serializable
}
