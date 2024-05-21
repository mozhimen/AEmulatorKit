package com.mozhimen.emulatork.test.hilt.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesHelper
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @ClassName CoresSelectionFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
class CoresSelectionFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var coresSelectionPreferences: com.mozhimen.emulatork.ui.settings.CoresSelectionPreferences

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesHelper.getSharedPreferencesDataStore(requireContext())
        setPreferencesFromResource(com.mozhimen.emulatork.ui.R.xml.empty_preference_screen, rootKey)
        coresSelectionPreferences.addCoresSelectionPreferences(preferenceScreen)
    }

    @dagger.Module
    class Module
}
