package com.mozhimen.emulatork.common.system

import androidx.annotation.StringRes
import com.mozhimen.emulatork.basic.system.ESystemType
import com.mozhimen.emulatork.basic.system.SystemScanOption
import com.mozhimen.emulatork.common.core.CoreBundle

/**
 * @ClassName GameSystem
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
data class SystemBundle constructor(
    val eSystemType: ESystemType,
    val libretroFullName: String,
    @StringRes
    val titleResId: Int,
    @StringRes
    val shortTitleResId: Int,
    val coreBundles: List<CoreBundle>,
    val uniqueFileExtNames: List<String>,
    val systemScanOption: SystemScanOption = SystemScanOption(),
    val supportRomExtNames: List<String> = uniqueFileExtNames,
    val hasMultiDiskSupport: Boolean = false,
    val fastForwardSupport: Boolean = true,
)