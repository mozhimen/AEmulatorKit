package com.mozhimen.emulatork.basic.controller

import java.io.Serializable

/**
 * @ClassName ControllerConfig
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class ControllerConfig(
    val name: String,
    val displayName: Int,
    val touchControllerID: TouchControllerID,
    val allowTouchRotation: Boolean = false,
    val allowTouchOverlay: Boolean = true,
    val mergeDPADAndLeftStickEvents: Boolean = false,
    val libretroDescriptor: String? = null,
    val libretroId: Int? = null
) : Serializable {

    fun getTouchControllerConfig(): TouchControllerID.Config {
        return TouchControllerID.getConfig(touchControllerID)
    }
}
