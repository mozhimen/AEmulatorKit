package com.mozhimen.emulatork.common.core

import com.mozhimen.emulatork.basic.system.SystemSetting
import com.mozhimen.emulatork.core.ECoreType
import com.mozhimen.emulatork.core.property.CoreProperty
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadConfig
import java.io.Serializable

/**
 * @ClassName SystemCoreConfig
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class CoreBundle constructor(
    val eCoreType: ECoreType,
    val gamepadConfigMap: HashMap<Int, ArrayList<GamepadConfig>>,
    val systemSettings_exposed: List<SystemSetting> = listOf(),
    val systemSettings_exposedAdvanced: List<SystemSetting> = listOf(),
    val coreProperties: List<CoreProperty> = listOf(),
    val isSupportStates: Boolean = true,
    val isSupportRumble: Boolean = false,
    val requiredBIOSFiles: List<String> = listOf(),
    val regionalBIOSFiles: Map<String, String> = mapOf(),
    val statesVersion: Int = 0,
    val supportsLibretroVFS: Boolean = false,
    val skipDuplicateFrames: Boolean = true,
    val supportedOnlyArchitectures: Set<String>? = null
) : Serializable
