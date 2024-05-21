package com.mozhimen.emulatork.test.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesHelper
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.settings.AdvancedSettingsPreferences
import com.mozhimen.emulatork.ui.settings.SettingsInteractor
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @ClassName AdvancedSettingsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
class AdvancedSettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesHelper.getSharedPreferencesDataStore(requireContext())
        setPreferencesFromResource(R.xml.mobile_settings_advanced, rootKey)
        AdvancedSettingsPreferences.updateCachePreferences(preferenceScreen)
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
                settingsInteractor.resetAllSettings()
                reloadPreferences()
            }
            .setNegativeButton(com.mozhimen.emulatork.ui.R.string.cancel) { _, _ -> }
            .show()
    }

    private fun reloadPreferences() {
        preferenceScreen = null
        setPreferencesFromResource(R.xml.mobile_settings, null)
    }

    @dagger.Module
    class Module
}