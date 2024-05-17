package com.mozhimen.emulatork.ui.dagger.feature.settings

import android.content.Context
import android.content.SharedPreferences
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.mozhimen.emulatork.basic.storage.cache.CacheCleaner
import com.mozhimen.emulatork.ui.R
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * @ClassName SettingsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class SettingsManager(private val context: Context, sharedPreferences: Lazy<SharedPreferences>) {

    private val sharedPreferences by lazy { FlowSharedPreferences(sharedPreferences.get()) }

    private fun getString(resId: Int) = context.getString(resId)

    suspend fun autoSave() = booleanPreference(R.string.pref_key_autosave, true)

    suspend fun hapticFeedbackMode() = stringPreference(R.string.pref_key_haptic_feedback_mode, "press")

    suspend fun lowLatencyAudio() = booleanPreference(R.string.pref_key_low_latency_audio, false)

    suspend fun screenFilter() = stringPreference(
        R.string.pref_key_shader_filter,
        context.resources.getStringArray(R.array.pref_key_shader_filter_values).first()
    )

    suspend fun hdMode() = booleanPreference(R.string.pref_key_hd_mode, false)

    suspend fun forceLegacyHdMode() = booleanPreference(R.string.pref_key_legacy_hd_mode, false)

    suspend fun tiltSensitivity() = floatPreference(R.string.pref_key_tilt_sensitivity_index, 10, 0.6f)

    suspend fun autoSaveSync() = booleanPreference(R.string.pref_key_save_sync_auto, false)

    suspend fun syncSaves() = booleanPreference(R.string.pref_key_save_sync_enable, true)

    suspend fun syncStatesCores() = stringSetPreference(R.string.pref_key_save_sync_cores, setOf())

    suspend fun enableRumble() = booleanPreference(R.string.pref_key_enable_rumble, false)

    suspend fun enableDeviceRumble() = booleanPreference(R.string.pref_key_enable_device_rumble, false)

    suspend fun cacheSizeBytes() = stringPreference(
        R.string.pref_key_max_cache_size,
        CacheCleaner.getDefaultCacheLimit().toString()
    )

    suspend fun allowDirectGameLoad() = booleanPreference(R.string.pref_key_allow_direct_game_load, true)

    private suspend fun booleanPreference(
        keyId: Int,
        default: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        sharedPreferences.getBoolean(getString(keyId), default)
            .asFlow()
            .first()
    }

    private suspend fun stringPreference(
        keyId: Int,
        default: String
    ): String = withContext(Dispatchers.IO) {
        sharedPreferences.getString(getString(keyId), default)
            .asFlow()
            .first()
    }

    private suspend fun stringSetPreference(
        keyId: Int,
        default: Set<String>
    ): Set<String> = withContext(Dispatchers.IO) {
        sharedPreferences.getStringSet(getString(keyId), default)
            .asFlow()
            .first()
    }

    private suspend fun floatPreference(
        keyId: Int,
        ticks: Int,
        default: Float
    ): Float = withContext(Dispatchers.IO) {
        sharedPreferences.getInt(getString(keyId), floatToIndex(default, ticks))
            .asFlow()
            .map { indexToFloat(it, ticks) }
            .first()
    }

    private fun indexToFloat(index: Int, ticks: Int): Float = index.toFloat() / ticks.toFloat()

    private fun floatToIndex(value: Float, ticks: Int): Int = (value * ticks).roundToInt()
}
