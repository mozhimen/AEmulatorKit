package com.mozhimen.emulatork.basic.archive

import kotlinx.serialization.Serializable

/**
 * @ClassName SaveState
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class ArchiveState(val state: ByteArray, val metadata: ArchiveMetadata) {
    @Serializable
    data class ArchiveMetadata(val diskIndex: Int = 0, val version: Int = 0)
}
