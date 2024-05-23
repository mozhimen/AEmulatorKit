package com.mozhimen.emulatork.basic.controller.touch

import java.io.Serializable

/**
 * @ClassName ControllerConfig
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class ControllerTouchConfig(
    val name: String,
    val displayName: Int,
    val touchControllerID: ControllerTouchID,
    val allowTouchRotation: Boolean = false,
    val allowTouchOverlay: Boolean = true,
    val mergeDPADAndLeftStickEvents: Boolean = false,
    val libretroDescriptor: String? = null,
    val libretroId: Int? = null
) : Serializable {

    fun getTouchControllerConfig(): ControllerTouchID.Config {
        return ControllerTouchID.getConfig(touchControllerID)
    }
}
