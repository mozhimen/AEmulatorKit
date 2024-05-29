package com.mozhimen.emulatork.test.dagger.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesMgr
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManager
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.ext.works.WorkPendingOperationsMonitor
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ext.preferences.PreferencesSaveSync
import com.mozhimen.emulatork.ui.dagger.works.WorkSaveSync
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
    lateinit var storageProvider: StorageProvider

    @Inject
    lateinit var saveSyncManager: SaveSyncManager
    private lateinit var saveSyncPreferences: PreferencesSaveSync

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesMgr.getSharedPreferencesDataStore(requireContext())

        saveSyncPreferences = PreferencesSaveSync(saveSyncManager)
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

    @dagger.Module
    class Module
}
