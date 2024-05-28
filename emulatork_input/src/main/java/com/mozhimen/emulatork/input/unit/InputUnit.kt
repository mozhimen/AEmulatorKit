package com.mozhimen.emulatork.input.unit

import android.content.Context
import android.view.InputDevice
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.InputMenuShortcut
import com.mozhimen.emulatork.input.key.InputKeyRetro
import com.mozhimen.emulatork.input.type.InputType
import com.mozhimen.emulatork.input.type.InputTypeGamePad
import com.mozhimen.emulatork.input.type.InputTypeKeyboard
import com.mozhimen.emulatork.input.type.InputTypeUnknown

/**
 * @ClassName LemuroidInputDevice
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface InputUnit {

    fun getCustomizableKeys(): List<InputKeyRetro>

    fun getDefaultBindings(): Map<InputKey, InputKeyRetro>

    fun isSupported(): Boolean

    fun isEnabledByDefault(appContext: Context): Boolean

    fun getSupportedShortcuts(): List<InputMenuShortcut>
}

fun InputDevice?.getEmulatorKInputDevice(): InputUnit {
    return when {
        this == null -> InputUnitUnknown
        (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD -> InputUnitGamePad(this)
        (sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD -> InputUnitKeyboard(this)
        else -> InputUnitUnknown
    }
}

fun InputDevice?.getInputType(): InputType {
    return when {
        this == null -> InputTypeUnknown
        (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD -> InputTypeGamePad
        (sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD -> InputTypeKeyboard
        else -> InputTypeUnknown
    }
}

fun InputDevice.supportsAllKeys(inputKeys: List<InputKey>): Boolean {
    val supportedKeyCodes = inputKeys
        .map { it.keyCode }
        .toIntArray()

    return this.hasKeys(*supportedKeyCodes).all { it }
}
