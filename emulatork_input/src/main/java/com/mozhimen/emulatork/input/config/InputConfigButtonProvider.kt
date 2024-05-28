package com.mozhimen.emulatork.input.config

import android.view.KeyEvent
import com.mozhimen.emulatork.input.R
import com.swordfish.radialgamepad.library.config.ButtonConfig
import com.swordfish.radialgamepad.library.event.GestureType

/**
 * @ClassName InputConfigButtonProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/28
 * @Version 1.0
 */
object InputConfigButtonProvider {
    fun getSTART() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_START,
        iconId = R.drawable.button_start,
        contentDescription = "Start"
    )

    fun getSELECT() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_SELECT,
        iconId = R.drawable.button_select,
        contentDescription = "Select"
    )

    fun getMENU() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_MODE,
        iconId = R.drawable.button_menu,
        contentDescription = "Menu"
    )

    fun getCROSS() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_B,
        iconId = R.drawable.psx_cross,
        contentDescription = "Cross"
    )

    fun getSQUARE() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_Y,
        iconId = R.drawable.psx_square,
        contentDescription = "Square"
    )

    fun getTRIANGLE() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_X,
        iconId = R.drawable.psx_triangle,
        contentDescription = "Triangle"
    )

    fun getCIRCLE() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_A,
        iconId = R.drawable.psx_circle,
        contentDescription = "Circle"
    )

    fun getCOIN() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_SELECT,
        iconId = R.drawable.button_coin,
        contentDescription = "Coin"
    )

    fun getL() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_L1,
        label = "L",
        supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH)
    )

    fun getR() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_R1,
        label = "R",
        supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH)
    )

    fun getL1() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_L1,
        label = "L1",
        supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH)
    )

    fun getR1() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_R1,
        label = "R1",
        supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH)
    )

    fun getL2() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_L2,
        label = "L2",
        supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH)
    )

    fun getR2() = ButtonConfig(
        id = KeyEvent.KEYCODE_BUTTON_R2,
        label = "R2",
        supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH)
    )


}