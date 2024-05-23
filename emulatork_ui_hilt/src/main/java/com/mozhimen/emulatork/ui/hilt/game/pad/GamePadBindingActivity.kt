package com.mozhimen.emulatork.ui.hilt.game.pad
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mozhimen.emulatork.ui.game.pad.AbsGamePadBindingActivity
import com.mozhimen.emulatork.input.device.InputDeviceManager
import javax.inject.Inject

/**
 * @ClassName GamePadBindingActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class GamePadBindingActivity : AbsGamePadBindingActivity() {
    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    override fun inputDeviceManager(): InputDeviceManager {
        return inputDeviceManager
    }

//    @dagger.Module
//    abstract class Module
}