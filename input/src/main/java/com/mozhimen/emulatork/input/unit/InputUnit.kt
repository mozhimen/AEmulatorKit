package com.mozhimen.emulatork.input.unit

import android.content.Context
import com.mozhimen.emulatork.input.virtual.menu.Menu
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.key.InputKeyRetro

/**
 * @ClassName LemuroidInputDevice
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface InputUnit {
    fun getCustomizableKeys(): List<InputKeyRetro>

    fun getInputKeyMap(): Map<InputKey, InputKeyRetro>

    fun isSupported(): Boolean

    fun isEnabledByDefault(appContext: Context): Boolean

    fun getSupportMenus(): List<Menu>
}

