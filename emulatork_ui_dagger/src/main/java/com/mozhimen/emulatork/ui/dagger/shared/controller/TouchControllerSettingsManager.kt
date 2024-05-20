package com.mozhimen.emulatork.ui.dagger.shared.controller

import android.content.Context
import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.controller.TouchControllerID
import com.mozhimen.emulatork.basic.controller.ControllerParams
import com.mozhimen.emulatork.input.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import dagger.Lazy

/**
 * @ClassName TouchControllerSettingsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class TouchControllerSettingsManager(
    private val context: Context,
    private val controllerID: TouchControllerID,
    private val sharedPreferences: Lazy<SharedPreferences>,
    private val orientation: Orientation
) {
    enum class Orientation {
        PORTRAIT,
        LANDSCAPE
    }

    data class Settings(
        val scale: Float = ControllerParams.DEFAULT_SCALE,
        val rotation: Float = ControllerParams.DEFAULT_ROTATION,
        val marginX: Float = ControllerParams.DEFAULT_MARGIN_X,
        val marginY: Float = ControllerParams.DEFAULT_MARGIN_Y
    )

    suspend fun retrieveSettings(): Settings = withContext(Dispatchers.IO) {
        val sharedPreferences = sharedPreferences.get()
        Settings(
            scale = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_scale, orientation),
                    floatToIndex(ControllerParams.DEFAULT_SCALE)
                )
            ),
            rotation = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_rotation, orientation),
                    floatToIndex(ControllerParams.DEFAULT_ROTATION)
                )
            ),
            marginX = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_margin_x, orientation),
                    floatToIndex(ControllerParams.DEFAULT_MARGIN_X)
                )
            ),
            marginY = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_margin_y, orientation),
                    floatToIndex(ControllerParams.DEFAULT_MARGIN_Y)
                )
            )
        )
    }

    suspend fun storeSettings(settings: Settings): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.get().edit().apply {
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_scale, orientation),
                floatToIndex(settings.scale)
            ).apply()
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_rotation, orientation),
                floatToIndex(settings.rotation)
            ).apply()
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_margin_x, orientation),
                floatToIndex(settings.marginX)
            ).apply()
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_margin_y, orientation),
                floatToIndex(settings.marginY)
            ).apply()
        }
    }

    private fun indexToFloat(index: Int): Float = index / 100f

    private fun floatToIndex(value: Float): Int = (value * 100).roundToInt()



    private fun getPreferenceString(preferenceStringId: Int, orientation: Orientation): String {
        return "${context.getString(preferenceStringId)}_${controllerID}_${orientation.ordinal}"
    }
}
