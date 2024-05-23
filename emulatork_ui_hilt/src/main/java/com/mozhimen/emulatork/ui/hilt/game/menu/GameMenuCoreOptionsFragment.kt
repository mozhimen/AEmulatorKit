package com.mozhimen.emulatork.ui.hilt.game.menu

import com.mozhimen.emulatork.ui.game.menu.AbsGameMenuCoreOptionsFragment
import com.mozhimen.emulatork.input.device.InputDeviceManager
import javax.inject.Inject

/**
 * @ClassName GameMenuCoreOptionsFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:06
 * @Version 1.0
 */
open class GameMenuCoreOptionsFragment : AbsGameMenuCoreOptionsFragment() {
    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    override fun inputDeviceManager(): InputDeviceManager {
        return inputDeviceManager
    }

//    @dagger.Module
//    class Module
}