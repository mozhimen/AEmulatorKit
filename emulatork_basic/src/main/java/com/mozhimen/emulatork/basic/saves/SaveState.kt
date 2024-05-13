package com.mozhimen.emulatork.basic.saves

import kotlinx.serialization.Serializable

/**
 * @ClassName SaveState
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class SaveState(val state: ByteArray, val metadata: Metadata) {
    @Serializable
    data class Metadata(val diskIndex: Int = 0, val version: Int = 0)
}
