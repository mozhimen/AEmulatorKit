package com.mozhimen.emulatork.test.dagger.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.ext.preferences.PreferencesCoreSelection
import dagger.android.support.AndroidSupportInjection
import com.mozhimen.emulatork.ui.dagger.works.WorkCoreUpdate
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
    lateinit var coresSelectionPreferences: PreferencesCoreSelection

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesManager.getSharedPreferencesDataStore(requireContext())
        setPreferencesFromResource(com.mozhimen.emulatork.ui.R.xml.empty_preference_screen, rootKey)
        coresSelectionPreferences.addCoresSelectionPreferences(preferenceScreen, WorkCoreUpdate::class.java)
    }

    @dagger.Module
    class Module
}
