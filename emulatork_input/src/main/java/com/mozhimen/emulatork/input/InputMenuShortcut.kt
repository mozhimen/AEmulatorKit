package com.mozhimen.emulatork.input

import android.view.InputDevice
import com.mozhimen.emulatork.input.device.getLemuroidInputDevice
/**
 * @ClassName GameMenuShortcut
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
data class InputMenuShortcut(val name: String, val keys: Set<Int>) {

    companion object {

        fun getDefault(inputDevice: InputDevice): InputMenuShortcut? {
            return inputDevice.getLemuroidInputDevice()
                .getSupportedShortcuts()
                .firstOrNull { shortcut ->
                    if (shortcut!=null){
                        inputDevice.hasKeys(*(shortcut.keys.toIntArray())).all { it }
                    }else false
                }
        }

        fun findByName(device: InputDevice, name: String): InputMenuShortcut? {
            return device.getLemuroidInputDevice()
                .getSupportedShortcuts()
                .firstOrNull { it?.name == name }
        }
    }
}
