package com.mozhimen.emulatork.test.feature.settings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.documentfile.provider.DocumentFile
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.feature.library.LibraryIndexWork
import dagger.android.support.AndroidSupportInjection

/**
 * @ClassName SettingsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.retrograde_mobile_prefs, rootKey)
    }

    override fun onResume() {
        super.onResume()

        val countingPreference: Preference? = findPreference(getString(com.mozhimen.emulatork.basic.R.string.pref_key_extenral_folder))
        countingPreference?.summaryProvider = Preference.SummaryProvider<Preference> {
            val uriString = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(it.key, null)
            uriString?.let { getDisplayNameForFolderUri(Uri.parse(uriString)) } ?: getString(R.string.none)
        }
    }

    private fun getDisplayNameForFolderUri(uri: Uri) = DocumentFile.fromTreeUri(requireContext(), uri)?.name

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            getString(R.string.pref_key_rescan) -> handleRescan()
            getString(com.mozhimen.emulatork.basic.R.string.pref_key_extenral_folder) -> handleChangeExternalFolder()
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun handleChangeExternalFolder() {
        StorageFrameworkPickerLauncher.pickFolder(requireContext())
    }

    private fun handleRescan() {
        context?.let { LibraryIndexWork.enqueueUniqueWork(it) }
    }

    @dagger.Module
    class Module
}