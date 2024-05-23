package com.mozhimen.emulatork.input

import kotlinx.serialization.Serializable

/**
 * @ClassName RetroKey
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@Serializable
@JvmInline
value class InputRetroKey(val keyCode: Int)
