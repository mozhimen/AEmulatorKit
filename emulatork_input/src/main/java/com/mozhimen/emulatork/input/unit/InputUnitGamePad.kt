package com.mozhimen.emulatork.input.unit

import android.content.Context
import android.view.InputDevice
import android.view.KeyEvent
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.InputMenuShortcut
import com.mozhimen.emulatork.input.key.InputKeyRetro
import com.mozhimen.emulatork.input.utils.intKeyCodePair2inputKeyPair
import com.mozhimen.emulatork.input.utils.inputKeyListOf
import com.mozhimen.emulatork.input.utils.inputKeyRetroListOf

/**
 * @ClassName LemuroidInputDeviceGamePad
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class InputUnitGamePad(private val device: InputDevice) : InputUnit {

    override fun getDefaultBindings(): Map<InputKey, InputKeyRetro> {
        val allAvailableInputs = InputUnitManager.OUTPUT_KEYS
            .associate {
                InputKey(it.keyCode) to getDefaultBindingForKey(device, it)
            }

        val defaultOverride = intKeyCodePair2inputKeyPair(
            KeyEvent.KEYCODE_BUTTON_A to KeyEvent.KEYCODE_BUTTON_B,
            KeyEvent.KEYCODE_BUTTON_B to KeyEvent.KEYCODE_BUTTON_A,
            KeyEvent.KEYCODE_BUTTON_X to KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_BUTTON_Y to KeyEvent.KEYCODE_BUTTON_X
        )

        return allAvailableInputs + defaultOverride
    }

    private fun getDefaultBindingForKey(
        device: InputDevice,
        it: InputKeyRetro
    ): InputKeyRetro {
        val defaultBinding = if (device.hasKeys(it.keyCode).first()) {
            InputKeyRetro(it.keyCode)
        } else {
            InputKeyRetro(KeyEvent.KEYCODE_UNKNOWN)
        }
        return defaultBinding
    }

    override fun isEnabledByDefault(appContext: Context): Boolean {
        return device.supportsAllKeys(MINIMAL_KEYS_DEFAULT_ENABLED)
    }

    override fun getSupportedShortcuts(): List<InputMenuShortcut> = listOf(
        InputMenuShortcut(
            "L3 + R3",
            setOf(KeyEvent.KEYCODE_BUTTON_THUMBL, KeyEvent.KEYCODE_BUTTON_THUMBR)
        ),
        InputMenuShortcut(
            "Select + Start",
            setOf(KeyEvent.KEYCODE_BUTTON_START, KeyEvent.KEYCODE_BUTTON_SELECT)
        )
    )

    override fun isSupported(): Boolean {
        return sequenceOf(
            device.sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD,
            device.supportsAllKeys(MINIMAL_SUPPORTED_KEYS),
            device.isVirtual.not(),
            device.controllerNumber > 0
        ).all { it }
    }

    override fun getCustomizableKeys(): List<InputKeyRetro> {
        val deviceAxis = device.motionRanges
            .map { it.axis }
            .toSet()

        val keysMappedToAxis = device.getInputType().getAxesMap()
            .filter { it.key in deviceAxis }
            .map { it.value }
            .toSet()

        return CUSTOMIZABLE_KEYS
            .filter { it.keyCode !in keysMappedToAxis }
    }

    companion object {

        private val MINIMAL_SUPPORTED_KEYS = inputKeyListOf(
            KeyEvent.KEYCODE_BUTTON_A,
            KeyEvent.KEYCODE_BUTTON_B,
            KeyEvent.KEYCODE_BUTTON_X,
            KeyEvent.KEYCODE_BUTTON_Y,
        )

        private val MINIMAL_KEYS_DEFAULT_ENABLED = MINIMAL_SUPPORTED_KEYS + inputKeyListOf(
            KeyEvent.KEYCODE_BUTTON_START,
            KeyEvent.KEYCODE_BUTTON_SELECT,
        )

        private val CUSTOMIZABLE_KEYS: List<InputKeyRetro> = inputKeyRetroListOf(
            KeyEvent.KEYCODE_BUTTON_A,
            KeyEvent.KEYCODE_BUTTON_B,
            KeyEvent.KEYCODE_BUTTON_X,
            KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_BUTTON_START,
            KeyEvent.KEYCODE_BUTTON_SELECT,
            KeyEvent.KEYCODE_BUTTON_L1,
            KeyEvent.KEYCODE_BUTTON_L2,
            KeyEvent.KEYCODE_BUTTON_R1,
            KeyEvent.KEYCODE_BUTTON_R2,
            KeyEvent.KEYCODE_BUTTON_THUMBL,
            KeyEvent.KEYCODE_BUTTON_THUMBR,
            KeyEvent.KEYCODE_BUTTON_MODE,
        )
    }
}