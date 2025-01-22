package com.mozhimen.emulatork.basic.metadata

/**
 * @ClassName GameMetadata
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
data class Metadata constructor(
    val name: String?,
    val system: String?,
    val romName: String?,
    val developer: String?,
    val thumbnail: String?
)
