package com.mozhimen.emulatork.test.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesHelper
import com.mozhimen.emulatork.basic.savesync.SaveSyncManager
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.ui.library.PendingOperationsMonitor
import com.mozhimen.emulatork.ui.settings.SaveSyncPreferences
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @ClassName SaveSyncFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */

class SaveSyncFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var directoriesManager: DirectoriesManager
    @Inject
    lateinit var saveSyncManager: SaveSyncManager
    private lateinit var saveSyncPreferences: com.mozhimen.emulatork.ui.settings.SaveSyncPreferences

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesHelper.getSharedPreferencesDataStore(requireContext())

        saveSyncPreferences = com.mozhimen.emulatork.ui.settings.SaveSyncPreferences(saveSyncManager)
        setPreferencesFromResource(com.mozhimen.emulatork.ui.dagger.R.xml.empty_preference_screen, rootKey)
        saveSyncPreferences.addSaveSyncPreferences(preferenceScreen)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (saveSyncPreferences.onPreferenceTreeClick(activity, preference))
            return true

        return super.onPreferenceTreeClick(preference)
    }

    override fun onResume() {
        super.onResume()
        saveSyncPreferences.updatePreferences(preferenceScreen, false)
        com.mozhimen.emulatork.ui.library.PendingOperationsMonitor(requireContext()).anySaveOperationInProgress().observe(this) {
            saveSyncPreferences.updatePreferences(preferenceScreen, it)
        }
    }

    @dagger.Module
    class Module
}
