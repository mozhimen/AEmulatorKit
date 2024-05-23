package com.mozhimen.emulatork.test.dagger.settings

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.ext.preferences.PreferencesBios
import com.mozhimen.abilityk.jetpack.preference.SafePreferenceDataStore
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @ClassName BiosSettingsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
class BiosSettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var preferencesBios: PreferencesBios

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = SafePreferenceDataStore
        setPreferencesFromResource(com.mozhimen.emulatork.ui.R.xml.empty_preference_screen, rootKey)
        preferencesBios.addBiosPreferences(preferenceScreen)
    }

    @dagger.Module
    class Module
}
