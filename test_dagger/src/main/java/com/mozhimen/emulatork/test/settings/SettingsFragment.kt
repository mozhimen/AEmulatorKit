package com.mozhimen.emulatork.test.settings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesHelper
import com.mozhimen.emulatork.basic.savesync.SaveSyncManager
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.library.LibraryIndexScheduler
import com.mozhimen.emulatork.ui.settings.SettingsInteractor
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @ClassName SettingsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var settingsInteractor: SettingsInteractor
    @Inject
    lateinit var directoriesManager: DirectoriesManager
    @Inject
    lateinit var saveSyncManager: SaveSyncManager

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
        setPreferencesFromResource(R.xml.mobile_settings, rootKey)

        findPreference<Preference>(getString(R.string.pref_key_open_save_sync_settings))?.apply {
            isVisible = saveSyncManager.isSupported()
        }
    }

    override fun onResume() {
        super.onResume()

        val factory = SettingsViewModel.Factory(
            requireContext(),
            FlowSharedPreferences(
                SharedPreferencesHelper.getLegacySharedPreferences(requireContext())
            )
        )
        val settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        val currentDirectory: Preference? = findPreference(getString(com.mozhimen.emulatork.basic.R.string.pref_key_extenral_folder))
        val rescanPreference: Preference? = findPreference(getString(R.string.pref_key_rescan))
        val stopRescanPreference: Preference? = findPreference(getString(R.string.pref_key_stop_rescan))
        val displayBiosPreference: Preference? = findPreference(getString(R.string.pref_key_display_bios_info))
        val resetSettings: Preference? = findPreference(getString(R.string.pref_key_reset_settings))

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            settingsViewModel.currentFolder
                .collect {
                    currentDirectory?.summary = getDisplayNameForFolderUri(Uri.parse(it))
                        ?: getString(R.string.none)
                }
        }

        settingsViewModel.indexingInProgress.observe(this) {
            rescanPreference?.isEnabled = !it
            currentDirectory?.isEnabled = !it
            displayBiosPreference?.isEnabled = !it
            resetSettings?.isEnabled = !it
        }

        settingsViewModel.directoryScanInProgress.observe(this) {
            stopRescanPreference?.isVisible = it
            rescanPreference?.isVisible = !it
        }
    }

    private fun getDisplayNameForFolderUri(uri: Uri): String? {
        return runCatching {
            DocumentFile.fromTreeUri(requireContext(), uri)?.name
        }.getOrNull()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            getString(R.string.pref_key_rescan) -> rescanLibrary()
            getString(R.string.pref_key_stop_rescan) -> stopRescanLibrary()
            getString(com.mozhimen.emulatork.basic.R.string.pref_key_extenral_folder) -> handleChangeExternalFolder()
            getString(R.string.pref_key_open_gamepad_settings) -> handleOpenGamePadSettings()
            getString(R.string.pref_key_open_save_sync_settings) -> handleDisplaySaveSync()
            getString(R.string.pref_key_open_cores_selection) -> handleDisplayCorePage()
            getString(R.string.pref_key_display_bios_info) -> handleDisplayBiosInfo()
            getString(R.string.pref_key_advanced_settings) -> handleAdvancedSettings()
            getString(R.string.pref_key_reset_settings) -> handleResetSettings()
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun handleAdvancedSettings() {
        findNavController().navigate(com.mozhimen.emulatork.test.R.id.navigation_settings_advanced)
    }

    private fun handleDisplayBiosInfo() {
        findNavController().navigate(com.mozhimen.emulatork.test.R.id.navigation_settings_bios_info)
    }

    private fun handleDisplayCorePage() {
        findNavController().navigate(com.mozhimen.emulatork.test.R.id.navigation_settings_cores_selection)
    }

    private fun handleDisplaySaveSync() {
        findNavController().navigate(com.mozhimen.emulatork.test.R.id.navigation_settings_save_sync)
    }

    private fun handleOpenGamePadSettings() {
        findNavController().navigate(com.mozhimen.emulatork.test.R.id.navigation_settings_gamepad)
    }

    private fun handleChangeExternalFolder() {
        settingsInteractor.changeLocalStorageFolder()
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

    private fun rescanLibrary() {
        context?.let { com.mozhimen.emulatork.ui.library.LibraryIndexScheduler.scheduleLibrarySync(it) }
    }

    private fun stopRescanLibrary() {
        context?.let { com.mozhimen.emulatork.ui.library.LibraryIndexScheduler.cancelLibrarySync(it) }
    }

    @dagger.Module
    class Module
}
