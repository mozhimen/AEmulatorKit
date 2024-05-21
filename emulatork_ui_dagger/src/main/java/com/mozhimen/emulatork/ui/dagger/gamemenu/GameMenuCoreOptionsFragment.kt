package com.mozhimen.emulatork.ui.dagger.gamemenu

import android.content.Context
import dagger.android.support.AndroidSupportInjection
import com.mozhimen.emulatork.ui.gamemenu.GameMenuCoreOptionsFragment
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import javax.inject.Inject

/**
 * @ClassName GameMenuCoreOptionsFragment
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:06
 * @Version 1.0
 */
open class GameMenuCoreOptionsFragment : GameMenuCoreOptionsFragment() {
    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    override fun getInputDeviceManager(): InputDeviceManager {
        return inputDeviceManager
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @dagger.Module
    class Module
}