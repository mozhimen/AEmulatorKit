package com.mozhimen.emulatork.test.feature.settings

import android.view.InputDevice
import com.mozhimen.emulatork.test.feature.input.lemuroiddevice.getLemuroidInputDevice

/**
 * @ClassName GameMenuShortcut
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
data class GameMenuShortcut(val name: String, val keys: Set<Int>) {

    companion object {

        fun getDefault(inputDevice: InputDevice): GameMenuShortcut? {
            return inputDevice.getLemuroidInputDevice()
                .getSupportedShortcuts()
                .firstOrNull { shortcut ->
                    inputDevice.hasKeys(*(shortcut.keys.toIntArray())).all { it }
                }
        }

        fun findByName(device: InputDevice, name: String): GameMenuShortcut? {
            return device.getLemuroidInputDevice()
                .getSupportedShortcuts()
                .firstOrNull { it.name == name }
        }
    }
}
