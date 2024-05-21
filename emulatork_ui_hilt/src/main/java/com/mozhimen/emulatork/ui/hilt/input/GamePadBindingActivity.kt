package com.mozhimen.emulatork.ui.hilt.input

import com.mozhimen.emulatork.ui.input.GamePadBindingActivity
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import javax.inject.Inject

/**
 * @ClassName GamePadBindingActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class GamePadBindingActivity :GamePadBindingActivity() {
    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    override fun inputDeviceManager(): InputDeviceManager {
        return inputDeviceManager
    }

//    @dagger.Module
//    abstract class Module
}