package com.mozhimen.emulatork.basic.dagger.controller

import android.content.Context
import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.controller.TouchControllerID
import com.mozhimen.emulatork.basic.controller.TouchControllerSettingsManager
import dagger.Lazy

/**
 * @ClassName TouchControllerSettingsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/20
 * @Version 1.0
 */
class TouchControllerSettingsManagerDagger(
    private val context: Context,
    private val controllerID: TouchControllerID,
    private val sharedPreferences: Lazy<SharedPreferences>,
    private val orientation: TouchControllerSettingsManager.Orientation
) : TouchControllerSettingsManager(context, controllerID, lazy { sharedPreferences.get() }, orientation)