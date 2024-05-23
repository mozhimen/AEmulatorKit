package com.mozhimen.emulatork.basic.game.system

/**
 * @ClassName SystemScanOptions
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/22
 * @Version 1.0
 */
data class GameSystemScanOptions(
    val scanByFilename: Boolean = true,
    val scanByUniqueExtension: Boolean = true,
    val scanByPathAndFilename: Boolean = false,
    val scanByPathAndSupportedExtensions: Boolean = true,
    val scanBySimilarSerial: Boolean = false
)