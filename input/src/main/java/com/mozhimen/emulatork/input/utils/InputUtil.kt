package com.mozhimen.emulatork.input.utils

import android.view.InputDevice
import com.mozhimen.emulatork.input.virtual.menu.Menu
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.key.InputKeyRetro
import com.mozhimen.emulatork.input.key.InputKeyMap
import com.mozhimen.emulatork.input.key.InputKeyMapGamePad
import com.mozhimen.emulatork.input.key.InputKeyMapKeyboard
import com.mozhimen.emulatork.input.key.InputKeyMapUnknown
import com.mozhimen.emulatork.input.unit.InputUnit
import com.mozhimen.emulatork.input.unit.InputUnitGamePad
import com.mozhimen.emulatork.input.unit.InputUnitKeyboard
import com.mozhimen.emulatork.input.unit.InputUnitUnknown

/**
 * @ClassName InputUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
internal fun inputKeySetOf(vararg keyCodes: Int): Set<InputKey> =
    inputKeyListOf(*keyCodes).toSet()

internal fun inputKeyListOf(vararg keyCodes: Int): List<InputKey> =
    keyCodes.map(::InputKey)

internal fun inputKeyRetroListOf(vararg keyCodes: Int): List<InputKeyRetro> =
    keyCodes.map(::InputKeyRetro)

internal fun intKeyCodePair2inputKeyPair(vararg bindings: Pair<Int, Int>) =
    bindings.associate {
        InputKey(it.first) to InputKeyRetro(it.second)
    }

fun InputDevice?.getInputUnit(): InputUnit {
    return when {
        this == null -> InputUnitUnknown
        (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD -> InputUnitGamePad(this)
        (sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD -> InputUnitKeyboard(this)
        else -> InputUnitUnknown
    }
}

fun InputDevice?.getInputKeyMap(): InputKeyMap {
    return when {
        this == null -> InputKeyMapUnknown
        (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD -> InputKeyMapGamePad
        (sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD -> InputKeyMapKeyboard
        else -> InputKeyMapUnknown
    }
}

fun InputDevice.isInputKeysSupport(inputKeys: List<InputKey>): Boolean {
    val supportedKeyCodes = inputKeys
        .map { it.keyCode }
        .toIntArray()
    return this.hasKeys(*supportedKeyCodes).all { it }
}

fun InputDevice.getDefaultMenu(): Menu? {
    return this.getInputUnit()
        .getSupportMenus()
        .firstOrNull { shortcut ->
            if (shortcut!=null){
                this.hasKeys(*(shortcut.keys.toIntArray())).all { it }
            }else false
        }
}

fun InputDevice.findMenuByName(name: String): Menu? {
    return this.getInputUnit()
        .getSupportMenus()
        .firstOrNull { it?.name == name }
}
