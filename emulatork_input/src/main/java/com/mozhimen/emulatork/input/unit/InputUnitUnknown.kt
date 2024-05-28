package com.mozhimen.emulatork.input.unit

import android.content.Context
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.InputMenuShortcut
import com.mozhimen.emulatork.input.key.InputKeyRetro

/**
 * @ClassName LemuroidInputDeviceUnknown
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object InputUnitUnknown : InputUnit {
    override fun getDefaultBindings(): Map<InputKey, InputKeyRetro> = emptyMap()

    override fun isSupported(): Boolean = false

    override fun isEnabledByDefault(appContext: Context): Boolean = false

    override fun getSupportedShortcuts(): List<InputMenuShortcut> = emptyList()

    override fun getCustomizableKeys(): List<InputKeyRetro> = emptyList()
}
