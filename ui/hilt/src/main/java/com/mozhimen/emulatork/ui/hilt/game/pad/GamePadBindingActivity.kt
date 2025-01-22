package com.mozhimen.emulatork.ui.hilt.game.pad
import com.mozhimen.emulatork.ui.game.pad.AbsGamePadBindingActivity
import com.mozhimen.emulatork.input.unit.InputUnitManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName GamePadBindingActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
@AndroidEntryPoint
class GamePadBindingActivity : AbsGamePadBindingActivity() {
    @Inject
    lateinit var inputUnitManager: InputUnitManager

    override fun inputDeviceManager(): InputUnitManager {
        return inputUnitManager
    }

//    @dagger.Module
//    abstract class Module
}