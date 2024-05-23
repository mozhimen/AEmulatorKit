package com.mozhimen.emulatork.input.device

import android.content.Context
import com.mozhimen.emulatork.input.InputKey
import com.mozhimen.emulatork.input.InputMenuShortcut
import com.mozhimen.emulatork.input.InputRetroKey

/**
 * @ClassName LemuroidInputDeviceUnknown
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object InputDeviceUnknown : InputDevice {
    override fun getDefaultBindings(): Map<InputKey, InputRetroKey> = emptyMap()

    override fun isSupported(): Boolean = false

    override fun isEnabledByDefault(appContext: Context): Boolean = false

    override fun getSupportedShortcuts(): List<InputMenuShortcut> = emptyList()

    override fun getCustomizableKeys(): List<InputRetroKey> = emptyList()
}
