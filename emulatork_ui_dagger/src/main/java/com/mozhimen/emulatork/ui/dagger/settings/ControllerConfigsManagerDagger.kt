package com.mozhimen.emulatork.ui.dagger.settings

import android.content.SharedPreferences
import com.mozhimen.emulatork.ui.settings.ControllerConfigsManager
import dagger.Lazy

/**
 * @ClassName ControllerConfigsManagerDagger
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/20
 * @Version 1.0
 */
class ControllerConfigsManagerDagger(private val sharedPreferences: Lazy<SharedPreferences>) : ControllerConfigsManager(lazy { sharedPreferences.get() })