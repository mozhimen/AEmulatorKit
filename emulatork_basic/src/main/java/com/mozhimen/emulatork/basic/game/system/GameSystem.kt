package com.mozhimen.emulatork.basic.game.system

import androidx.annotation.StringRes

/**
 * @ClassName GameSystem
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
data class GameSystem(
    val id: GameSystemID,
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