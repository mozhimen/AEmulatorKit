package com.mozhimen.emulatork.ui.gamemenu

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.basic.library.SystemCoreConfig
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.game.GameMenuContract
import com.mozhimen.abilityk.jetpack.preference.SafePreferenceDataStore

/**
 * @ClassName GameMenuFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsGameMenuFragment : PreferenceFragmentCompat() {

//    override fun onAttach(context: Context) {
//        AndroidSupportInjection.inject(this)
//        super.onAttach(context)
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = SafePreferenceDataStore
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


}