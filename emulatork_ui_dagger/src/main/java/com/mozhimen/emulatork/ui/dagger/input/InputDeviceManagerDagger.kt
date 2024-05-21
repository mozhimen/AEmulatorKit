package com.mozhimen.emulatork.ui.dagger.input

import android.content.Context
import android.content.SharedPreferences
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import dagger.Lazy

/**
 * @ClassName InputDeviceManager
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 21:31
 * @Version 1.0
 */
class InputDeviceManagerDagger(
    private val context: Context,
    sharedPreferencesFactory: Lazy<SharedPreferences>
) :InputDeviceManager(context, lazy { sharedPreferencesFactory.get() })