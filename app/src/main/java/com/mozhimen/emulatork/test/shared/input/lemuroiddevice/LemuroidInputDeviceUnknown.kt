package com.mozhimen.emulatork.test.shared.input.lemuroiddevice

import android.content.Context
import com.mozhimen.emulatork.test.shared.input.InputKey
import com.mozhimen.emulatork.test.shared.input.RetroKey
import com.mozhimen.emulatork.test.shared.settings.GameMenuShortcut

/**
 * @ClassName LemuroidInputDeviceUnknown
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object LemuroidInputDeviceUnknown : LemuroidInputDevice {
    override fun getDefaultBindings(): Map<InputKey, RetroKey> = emptyMap()

    override fun isSupported(): Boolean = false

    override fun isEnabledByDefault(appContext: Context): Boolean = false

    override fun getSupportedShortcuts(): List<GameMenuShortcut> = emptyList()

    override fun getCustomizableKeys(): List<RetroKey> = emptyList()
}
