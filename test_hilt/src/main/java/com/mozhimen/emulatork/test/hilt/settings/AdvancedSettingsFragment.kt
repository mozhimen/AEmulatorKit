package com.mozhimen.emulatork.test.hilt.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesMgr
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ext.preferences.PreferencesAdvancedSettings
import com.mozhimen.emulatork.ext.library.SettingsInteractor
import com.mozhimen.emulatork.test.hilt.works.WorkLibraryIndex
import com.mozhimen.emulatork.test.hilt.works.WorkStorageCacheCleaner
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName AdvancedSettingsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
@AndroidEntryPoint
class AdvancedSettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesMgr.getSharedPreferencesDataStore(requireContext())
        setPreferencesFromResource(R.xml.mobile_settings_advanced, rootKey)
        PreferencesAdvancedSettings.updateCachePreferences(preferenceScreen)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            getString(R.string.pref_key_reset_settings) -> handleResetSettings()
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun handleResetSettings() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.reset_settings_warning_message_title)
            .setMessage(R.string.reset_settings_warning_message_description)
            .setPositiveButton(R.string.ok) { _, _ ->
                settingsInteractor.resetAllSettings(WorkLibraryIndex::class.java, WorkStorageCacheCleaner::class.java)
                reloadPreferences()
            }
            .setNegativeButton(com.mozhimen.emulatork.ui.R.string.cancel) { _, _ -> }
            .show()
    }

    private fun reloadPreferences() {
        preferenceScreen = null
        setPreferencesFromResource(R.xml.mobile_settings, null)
    }

//    @dagger.Module
//    class Module
}