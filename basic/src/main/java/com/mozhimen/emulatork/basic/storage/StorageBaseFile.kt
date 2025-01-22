package com.mozhimen.emulatork.basic.storage

import android.net.Uri

/**
 * @ClassName BaseStorageFile
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class StorageBaseFile constructor(
    val name: String,
    val size: Long,
    val uri: Uri,
    val path: String? = null
) {

    val extension: String
        get() = name.substringAfterLast('.', "")

    val extensionlessName: String
        get() = name.substringBeforeLast('.', "")
}
