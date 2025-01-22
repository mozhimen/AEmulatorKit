package com.mozhimen.emulatork.input.virtual.gamepad

import android.content.Context
import android.content.SharedPreferences
import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.type.EInputType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * @ClassName TouchControllerSettingsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
open class GamepadSettingManager(
    private val context: Context,
    private val inputType: EInputType,
    private val sharedPreferences: Lazy<SharedPreferences>,
    private val gamepadOrientation: EGamepadOrientation
) {
    suspend fun retrieveSetting(): GamepadSetting = withContext(Dispatchers.IO) {
        val sharedPreferences = sharedPreferences.value
        GamepadSetting(
            scale = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_scale, gamepadOrientation),
                    floatToIndex(GamepadSetting.DEFAULT_SCALE)
                )
            ),
            rotation = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_rotation, gamepadOrientation),
                    floatToIndex(GamepadSetting.DEFAULT_ROTATION)
                )
            ),
            marginX = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_margin_x, gamepadOrientation),
                    floatToIndex(GamepadSetting.DEFAULT_MARGIN_X)
                )
            ),
            marginY = indexToFloat(
                sharedPreferences.getInt(
                    getPreferenceString(R.string.pref_key_virtual_pad_margin_y, gamepadOrientation),
                    floatToIndex(GamepadSetting.DEFAULT_MARGIN_Y)
                )
            )
        )
    }

    suspend fun storeSetting(setting: GamepadSetting): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.value.edit().apply {
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_scale, gamepadOrientation),
                floatToIndex(setting.scale)
            ).apply()
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_rotation, gamepadOrientation),
                floatToIndex(setting.rotation)
            ).apply()
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_margin_x, gamepadOrientation),
                floatToIndex(setting.marginX)
            ).apply()
            putInt(
                getPreferenceString(com.mozhimen.emulatork.input.R.string.pref_key_virtual_pad_margin_y, gamepadOrientation),
                floatToIndex(setting.marginY)
            ).apply()
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private fun indexToFloat(index: Int): Float = index / 100f

    private fun floatToIndex(value: Float): Int = (value * 100).roundToInt()

    private fun getPreferenceString(preferenceStringId: Int, orientation: EGamepadOrientation): String {
        return "${context.getString(preferenceStringId)}_${inputType}_${orientation.ordinal}"
    }
}
