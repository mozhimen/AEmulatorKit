package com.mozhimen.emulatork.basic.storage

import android.net.Uri

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

    val uri: Uri
) {

    val extension: String
        get() = name.substringAfterLast('.', "")
}