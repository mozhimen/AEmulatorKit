package com.mozhimen.emulatork.input.unit

import android.content.Context
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.virtual.menu.Menu
import com.mozhimen.emulatork.input.key.InputKeyRetro

/**
 * @ClassName LemuroidInputDeviceUnknown
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object InputUnitUnknown : InputUnit {
    override fun getInputKeyMap(): Map<InputKey, InputKeyRetro> = emptyMap()

    override fun isSupported(): Boolean = false

    override fun isEnabledByDefault(appContext: Context): Boolean = false

    override fun getSupportMenus(): List<Menu> = emptyList()

    override fun getCustomizableKeys(): List<InputKeyRetro> = emptyList()
}
