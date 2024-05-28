package com.mozhimen.emulatork.ui.hilt.game.menu

import com.mozhimen.emulatork.ui.game.menu.AbsGameMenuCoreOptionsFragment
import com.mozhimen.emulatork.input.unit.InputUnitManager
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
    lateinit var inputUnitManager: InputUnitManager

    override fun inputDeviceManager(): InputUnitManager {
        return inputUnitManager
    }

//    @dagger.Module
//    class Module
}