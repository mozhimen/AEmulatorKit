package com.mozhimen.emulatork.core

import com.mozhimen.emulatork.basic.controller.touch.ControllerTouchConfig
import com.mozhimen.emulatork.core.variable.CoreVariable
import java.io.Serializable

/**
 * @ClassName SystemCoreConfig
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class CoreBundle constructor(
    val coreType: ECoreType,
    val controllerConfigs: HashMap<Int, ArrayList<ControllerTouchConfig>>,
    val exposedSystemSettings: List<GameSystemExposedSetting> = listOf(),
    val exposedAdvancedSettings: List<GameSystemExposedSetting> = listOf(),
    val defaultSettings: List<CoreVariable> = listOf(),
    val statesSupported: Boolean = true,
    val rumbleSupported: Boolean = false,
    val requiredBIOSFiles: List<String> = listOf(),
    val regionalBIOSFiles: Map<String, String> = mapOf(),
    val statesVersion: Int = 0,
    val supportsLibretroVFS: Boolean = false,
    val skipDuplicateFrames: Boolean = true,
    val supportedOnlyArchitectures: Set<String>? = null
) : Serializable
