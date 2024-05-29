package com.mozhimen.emulatork.common.system

import androidx.annotation.StringRes
import com.mozhimen.emulatork.basic.system.ESystemType

/**
 * @ClassName GameSystem
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
data class SystemBundle(
    val id: ESystemType,
    val libretroFullName: String,
    @StringRes
    val titleResId: Int,
    @StringRes
    val shortTitleResId: Int,
    val systemCoreConfigs: List<GameSystemCoreConfig>,
    val uniqueExtensions: List<String>,
    val scanOptions: GameSystemScanOptions = GameSystemScanOptions(),
    val supportedExtensions: List<String> = uniqueExtensions,
    val hasMultiDiskSupport: Boolean = false,
    val fastForwardSupport: Boolean = true,
)