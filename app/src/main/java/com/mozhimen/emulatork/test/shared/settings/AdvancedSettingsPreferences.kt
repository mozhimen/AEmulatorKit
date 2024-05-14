package com.mozhimen.emulatork.test.shared.settings

import android.content.Context
import android.text.format.Formatter
import androidx.preference.ListPreference
import androidx.preference.PreferenceScreen
import com.mozhimen.emulatork.basic.storage.cache.CacheCleaner
import com.mozhimen.emulatork.test.R

/**
 * @ClassName AdvancedSettingsPreferences
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
object AdvancedSettingsPreferences {

    fun updateCachePreferences(preferenceScreen: PreferenceScreen) {
        val cacheKey = preferenceScreen.context.getString(R.string.pref_key_max_cache_size)
        preferenceScreen.findPreference<ListPreference>(cacheKey)?.apply {
            val supportedCacheValues = CacheCleaner.getSupportedCacheLimits()

            entries = supportedCacheValues
                .map { getSizeLabel(preferenceScreen.context, it) }
                .toTypedArray()

            entryValues = supportedCacheValues
                .map { it.toString() }
                .toTypedArray()

            if (value == null) {
                setValueIndex(supportedCacheValues.indexOf(CacheCleaner.getDefaultCacheLimit()))
            }

            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }
    }

    private fun getSizeLabel(appContext: Context, size: Long): String {
        return Formatter.formatShortFileSize(appContext, size)
    }
}
