package com.mozhimen.emulatork.test.shared.input

import kotlinx.serialization.Serializable

/**
 * @ClassName InputKey
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@Serializable
@JvmInline
value class InputKey(val keyCode: Int)
