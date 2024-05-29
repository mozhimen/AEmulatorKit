package com.mozhimen.emulatork.input.virtual.gamepad

/**
 * @ClassName GamepadSetting
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/29
 * @Version 1.0
 */
data class GamepadSetting constructor(
    val scale: Float = DEFAULT_SCALE,
    val rotation: Float = DEFAULT_ROTATION,
    val marginX: Float = DEFAULT_MARGIN_X,
    val marginY: Float = DEFAULT_MARGIN_Y
) {
    companion object {
        const val DEFAULT_SCALE = 0.5f
        const val DEFAULT_ROTATION = 0.0f
        const val DEFAULT_MARGIN_X = 0.0f
        const val DEFAULT_MARGIN_Y = 0.0f

        const val MAX_ROTATION = 45f
        const val MIN_SCALE = 0.75f
        const val MAX_SCALE = 1.5f

        const val MAX_MARGINS = 96f
    }
}