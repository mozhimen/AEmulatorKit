package com.mozhimen.emulatork.test.feature.gamemenu

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.library.SystemCoreConfig
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.shared.GameMenuContract
import com.mozhimen.emulatork.test.shared.gamemenu.GameMenuHelper
import com.mozhimen.emulatork.util.preferences.DummyDataStore
import dagger.android.support.AndroidSupportInjection

/**
 * @ClassName GameMenuFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameMenuFragment : PreferenceFragmentCompat() {

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = DummyDataStore
        setPreferencesFromResource(R.xml.mobile_game_settings, rootKey)

        val audioEnabled = activity?.intent?.getBooleanExtra(
            GameMenuContract.EXTRA_AUDIO_ENABLED,
            false
        ) ?: false

        GameMenuHelper.setupAudioOption(preferenceScreen, audioEnabled)

        val fastForwardSupported = activity?.intent?.getBooleanExtra(
            GameMenuContract.EXTRA_FAST_FORWARD_SUPPORTED,
            false
        ) ?: false

        val fastForwardEnabled = activity?.intent?.getBooleanExtra(
            GameMenuContract.EXTRA_FAST_FORWARD,
            false
        ) ?: false

        GameMenuHelper.setupFastForwardOption(
            preferenceScreen,
            fastForwardEnabled,
            fastForwardSupported
        )

        val systemCoreConfig = activity?.intent?.getSerializableExtra(
            GameMenuContract.EXTRA_SYSTEM_CORE_CONFIG
        ) as SystemCoreConfig

        GameMenuHelper.setupSaveOption(preferenceScreen, systemCoreConfig)

        val numDisks = activity?.intent?.getIntExtra(GameMenuContract.EXTRA_DISKS, 0) ?: 0
        val currentDisk = activity?.intent?.getIntExtra(GameMenuContract.EXTRA_CURRENT_DISK, 0) ?: 0
        if (numDisks > 1) {
            GameMenuHelper.setupChangeDiskOption(activity, preferenceScreen, currentDisk, numDisks)
        }

        GameMenuHelper.setupSettingsOption(preferenceScreen, systemCoreConfig)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (GameMenuHelper.onPreferenceTreeClicked(activity, preference))
            return true

        when (preference?.key) {
            "pref_game_section_save" -> {
                findNavController().navigate(R.id.game_menu_save)
            }
            "pref_game_section_load" -> {
                findNavController().navigate(R.id.game_menu_load)
            }
            "pref_game_section_core_options" -> {
                findNavController().navigate(R.id.game_menu_core_options)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    @dagger.Module
    class Module
}
