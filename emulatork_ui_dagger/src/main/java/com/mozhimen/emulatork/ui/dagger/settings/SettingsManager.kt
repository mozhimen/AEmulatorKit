package com.mozhimen.emulatork.ui.dagger.settings

import android.content.Context
import android.content.SharedPreferences
import com.mozhimen.emulatork.ui.settings.SettingsManager
import dagger.Lazy

/**
 * @ClassName SettingsManager
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:40
 * @Version 1.0
 */
class SettingsManager(context: Context, sharedPreferences: Lazy<SharedPreferences>) :SettingsManager(context, lazy { sharedPreferences.get() }){
}