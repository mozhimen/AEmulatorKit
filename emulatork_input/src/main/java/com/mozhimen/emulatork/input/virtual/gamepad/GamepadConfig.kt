package com.mozhimen.emulatork.input.virtual.gamepad

import com.mozhimen.emulatork.input.virtual.area.InputAreaConfig
import com.mozhimen.emulatork.input.type.EInputType
import java.io.Serializable

/**
 * @ClassName ControllerConfig
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class GamepadConfig(
    val name: String,
    val displayName: Int,
    val inputType: EInputType,
    val allowTouchRotation: Boolean = false,
    val allowTouchOverlay: Boolean = true,
    val mergeDPADAndLeftStickEvents: Boolean = false,
    val libretroDescriptor: String? = null,
    val libretroId: Int? = null
) : Serializable {
    fun getInputAreaConfig(): InputAreaConfig {
        return InputAreaConfig.getConfig(inputType)
    }
}
