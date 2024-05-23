package com.mozhimen.emulatork.input.device

import android.content.Context
import android.view.InputDevice
import com.mozhimen.emulatork.input.InputKey
import com.mozhimen.emulatork.input.InputMenuShortcut
import com.mozhimen.emulatork.input.InputRetroKey

/**
 * @ClassName LemuroidInputDevice
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface InputDevice {

    fun getCustomizableKeys(): List<InputRetroKey>

    fun getDefaultBindings(): Map<InputKey, InputRetroKey>

    fun isSupported(): Boolean

    fun isEnabledByDefault(appContext: Context): Boolean

    fun getSupportedShortcuts(): List<InputMenuShortcut>
}

fun InputDevice?.getLemuroidInputDevice(): com.mozhimen.emulatork.input.device.InputDevice {
    return when {
        this == null -> InputDeviceUnknown
        (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD -> InputDeviceGamePad(this)
        (sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD -> InputDeviceKeyboard(this)
        else -> InputDeviceUnknown
    }
}
