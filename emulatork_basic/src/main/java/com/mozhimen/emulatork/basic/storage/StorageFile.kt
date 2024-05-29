package com.mozhimen.emulatork.basic.storage

import android.net.Uri
import com.mozhimen.basick.utilk.kotlin.UtilKStrFile
import com.mozhimen.emulatork.basic.system.ESystemType

/**
 * @ClassName StorageFile
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
data class StorageFile(
    val name: String,
    val size: Long,
    val crc: String? = null,
    val serial: String? = null,
    val uri: Uri,
    val path: String? = null,
    val systemID: ESystemType? = null
) {

    val extension: String
        get() = UtilKStrFile.extractExtension(name)

    val extensionlessName: String
        get() = UtilKStrFile.discardExtension(name)
}
