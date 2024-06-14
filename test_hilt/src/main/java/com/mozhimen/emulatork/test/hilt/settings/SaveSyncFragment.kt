package com.mozhimen.emulatork.test.hilt.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.common.archive.ArchiveManager
import com.mozhimen.emulatork.ext.works.WorkPendingOperationsMonitor
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ext.preferences.PreferencesArchive
import com.mozhimen.emulatork.ui.hilt.works.WorkSaveSync
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName SaveSyncFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
@AndroidEntryPoint
class SaveSyncFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var storageProvider: StorageDirProvider

    @Inject
    lateinit var saveSyncManager: ArchiveManager
    private lateinit var saveSyncPreferences: PreferencesArchive

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesManager.getSharedPreferencesDataStore(requireContext())

        saveSyncPreferences = PreferencesArchive(saveSyncManager)
        setPreferencesFromResource(R.xml.empty_preference_screen, rootKey)
        saveSyncPreferences.addSaveSyncPreferences(preferenceScreen)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (saveSyncPreferences.onPreferenceTreeClick(activity, preference, WorkSaveSync::class.java))
            return true

        return super.onPreferenceTreeClick(preference)
    }

    override fun onResume() {
        super.onResume()
        saveSyncPreferences.updatePreferences(preferenceScreen, false)
        WorkPendingOperationsMonitor(requireContext()).anySaveOperationInProgress().observe(this) {
            saveSyncPreferences.updatePreferences(preferenceScreen, it)
        }
    }

//    @dagger.Module
//    class Module
}
