package com.mozhimen.emulatork.basic.storage

/**
 * @ClassName GroupedStorageFiles
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class GroupedStorageFiles(
    val primaryFile: BaseStorageFile,
    val dataFiles: List<BaseStorageFile>
) {
    fun allFiles() = listOf(primaryFile) + dataFiles
}
