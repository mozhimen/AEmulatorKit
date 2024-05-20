package com.mozhimen.emulatork.ui.input.lemuroiddevice

import android.content.Context
import android.view.InputDevice
import com.mozhimen.emulatork.ui.input.InputKey
import com.mozhimen.emulatork.ui.input.RetroKey
import com.mozhimen.emulatork.ui.dagger.shared.settings.GameMenuShortcut

/**
 * @ClassName LemuroidInputDevice
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface LemuroidInputDevice {

    fun getCustomizableKeys(): List<RetroKey>

    fun getDefaultBindings(): Map<InputKey, RetroKey>

    fun isSupported(): Boolean

    fun isEnabledByDefault(appContext: Context): Boolean

    fun getSupportedShortcuts(): List<GameMenuShortcut>
}

fun InputDevice?.getLemuroidInputDevice(): LemuroidInputDevice {
    return when {
        this == null -> LemuroidInputDeviceUnknown
        (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD -> LemuroidInputDeviceGamePad(this)
        (sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD -> LemuroidInputDeviceKeyboard(this)
        else -> LemuroidInputDeviceUnknown
    }
}
