package com.mozhimen.emulatork.ui.game.menu

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.emulatork.ui.R
import com.mozhimen.abilityk.jetpack.preference.SafePreferenceDataStore
import com.mozhimen.emulatork.basic.game.menu.GameMenuContract
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.ext.input.MenuMgr
import com.mozhimen.emulatork.input.virtual.menu.MenuContract
import com.mozhimen.libk.jetpack.preference.SafePreferenceDataStore

/**
 * @ClassName GameMenuFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsGameMenuFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = SafePreferenceDataStore
        setPreferencesFromResource(R.xml.mobile_game_settings, rootKey)

        val audioEnabled = activity?.intent?.getBooleanExtra(
            MenuContract.EXTRA_AUDIO_ENABLED,
            false
        ) ?: false

        MenuMgr.setupAudioOption(preferenceScreen, audioEnabled)

        val fastForwardSupported = activity?.intent?.getBooleanExtra(MenuContract.EXTRA_FAST_FORWARD_SUPPORTED, false) ?: false

        val fastForwardEnabled = activity?.intent?.getBooleanExtra(MenuContract.EXTRA_FAST_FORWARD, false) ?: false

        MenuMgr.setupFastForwardOption(
            preferenceScreen,
            fastForwardEnabled,
            fastForwardSupported
        )

        val coreBundle = activity?.intent?.getSerializableExtra(MenuContract.EXTRA_SYSTEM_CORE_BUNDLE) as? CoreBundle?

        MenuMgr.setupSaveOption(preferenceScreen, coreBundle)

        val numDisks = activity?.intent?.getIntExtra(MenuContract.EXTRA_DISKS, 0) ?: 0
        val currentDisk = activity?.intent?.getIntExtra(MenuContract.EXTRA_CURRENT_DISK, 0) ?: 0
        if (numDisks > 1) {
            MenuMgr.setupChangeDiskOption(activity, preferenceScreen, currentDisk, numDisks)
        }

        MenuMgr.setupSettingsOption(preferenceScreen, coreBundle)
    }
}
