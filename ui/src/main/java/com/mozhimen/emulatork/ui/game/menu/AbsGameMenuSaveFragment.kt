package com.mozhimen.emulatork.ui.game.menu

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.ui.R
import com.mozhimen.libk.jetpack.preference.SafePreferenceDataStore
import com.mozhimen.emulatork.common.save.SaveStateManager
import com.mozhimen.emulatork.common.save.SaveStatePreviewManager
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.ext.input.MenuMgr
import java.security.InvalidParameterException
import com.mozhimen.emulatork.input.virtual.menu.MenuContract

/**
 * @ClassName GameMenuSaveFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsGameMenuSaveFragment : PreferenceFragmentCompat() {

//    @Inject
//    lateinit var statesManager: StatesManager
    abstract fun statesManager(): SaveStateManager
//    @Inject
//    lateinit var statesPreviewManager: StatesPreviewManager
    abstract fun statesPreviewManager(): SaveStatePreviewManager

//    override fun onAttach(context: Context) {
//        AndroidSupportInjection.inject(this)
//        super.onAttach(context)
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = SafePreferenceDataStore
        addPreferencesFromResource(R.xml.empty_preference_screen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val extras = activity?.intent?.extras

        val game = extras?.getSerializable(MenuContract.EXTRA_GAME) as Game?
            ?: throw InvalidParameterException("Missing EXTRA_GAME")

        val coreBundle = extras?.getSerializable(MenuContract.EXTRA_SYSTEM_CORE_BUNDLE) as CoreBundle?
            ?: throw InvalidParameterException("Missing EXTRA_SYSTEM_CORE_CONFIG")

        runOnViewLifecycleState(Lifecycle.State.CREATED) {
            setupSavePreference(game, coreBundle)
        }
    }

    private suspend fun setupSavePreference(game: Game, coreBundle: CoreBundle) {
        val slotsInfo = statesManager().getSavedSlotsInfo(game, coreBundle.eCoreType)

        slotsInfo.forEachIndexed { index, saveInfo ->
            val bitmap = MenuMgr.getSaveStateBitmap(
                requireContext(),
                statesPreviewManager(),
                saveInfo,
                game,
                coreBundle.eCoreType,
                index
            )

            MenuMgr.addSavePreference(
                preferenceScreen,
                index,
                saveInfo,
                bitmap
            )
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return MenuMgr.onPreferenceTreeClicked(activity, preference)
    }
}
