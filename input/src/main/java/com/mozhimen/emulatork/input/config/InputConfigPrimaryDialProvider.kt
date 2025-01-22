package com.mozhimen.emulatork.input.config

import com.mozhimen.emulatork.input.R
import com.swordfish.radialgamepad.library.config.CrossConfig
import com.swordfish.radialgamepad.library.config.PrimaryDialConfig
import com.swordfish.radialgamepad.library.event.GestureType

/**
 * @ClassName InputConfigCrossProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/28
 * @Version 1.0
 */
object InputConfigPrimaryDialProvider {
    fun getCROSS() =
        PrimaryDialConfig.Cross(
            CrossConfig(
                id = InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD,
                shape = CrossConfig.Shape.CIRCLE,
                supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                rightDrawableForegroundId = R.drawable.direction_alt_foreground
            ),
        )

    fun getCROSS_MERGED() =
        PrimaryDialConfig.Cross(
            CrossConfig(
                id = InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD_AND_LEFT_STICK,
                shape = CrossConfig.Shape.CIRCLE,
                supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                rightDrawableForegroundId = R.drawable.direction_alt_foreground
            )
        )
}