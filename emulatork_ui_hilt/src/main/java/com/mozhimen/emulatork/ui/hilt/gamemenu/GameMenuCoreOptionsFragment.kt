package com.mozhimen.emulatork.ui.hilt.gamemenu

import com.mozhimen.emulatork.ui.gamemenu.AbsGameMenuCoreOptionsFragment
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName GameMenuCoreOptionsFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:06
 * @Version 1.0
 */
@AndroidEntryPoint
open class GameMenuCoreOptionsFragment : AbsGameMenuCoreOptionsFragment() {
    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    override fun inputDeviceManager(): InputDeviceManager {
        return inputDeviceManager
    }

//    @dagger.Module
//    class Module
}