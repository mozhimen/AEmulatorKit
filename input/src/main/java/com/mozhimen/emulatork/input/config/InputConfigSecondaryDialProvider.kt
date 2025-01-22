package com.mozhimen.emulatork.input.config

import com.swordfish.radialgamepad.library.config.RadialGamePadTheme
import com.swordfish.radialgamepad.library.config.SecondaryDialConfig

/**
 * @ClassName InputConfigSecondaryDialProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/28
 * @Version 1.0
 */
object InputConfigSecondaryDialProvider {
    fun getSingleButtonMenu(index: Int, theme: RadialGamePadTheme): SecondaryDialConfig =
        SecondaryDialConfig.SingleButton(
            index = index,
            scale = 1f,
            distance = 0f,
            buttonConfig = InputConfigButtonProvider.getMENU(),
            rotationProcessor = getRotationProcessorRotationInvert(),
            theme = theme
        )

    fun getRotationProcessorRotationOffset(degrees: Float): SecondaryDialConfig.RotationProcessor =
        object : SecondaryDialConfig.RotationProcessor() {
            override fun getRotation(rotation: Float): Float {
                return rotation + degrees
            }
        }

    fun getRotationProcessorRotationInvert(): SecondaryDialConfig.RotationProcessor =
        object : SecondaryDialConfig.RotationProcessor() {
            override fun getRotation(rotation: Float): Float {
                return -rotation
            }
        }
}