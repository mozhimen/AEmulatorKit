package com.mozhimen.emulatork.test.hilt.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.ext.preferences.PreferencesCoreSelection
import com.mozhimen.emulatork.test.hilt.works.WorkCoreUpdate
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName CoresSelectionFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
@AndroidEntryPoint
class CoresSelectionFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var coresSelectionPreferences: PreferencesCoreSelection

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesManager.getSharedPreferencesDataStore(requireContext())
        setPreferencesFromResource(com.mozhimen.emulatork.ui.R.xml.empty_preference_screen, rootKey)
        coresSelectionPreferences.addCoresSelectionPreferences(preferenceScreen, WorkCoreUpdate::class.java)
    }

//    @dagger.Module
//    class Module
}
