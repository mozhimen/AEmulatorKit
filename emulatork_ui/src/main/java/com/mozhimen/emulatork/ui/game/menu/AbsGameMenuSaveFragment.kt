package com.mozhimen.emulatork.ui.game.menu

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.ui.R
import com.mozhimen.abilityk.jetpack.preference.SafePreferenceDataStore
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.save.SaveStatePreviewManager
import com.mozhimen.emulatork.basic.game.menu.GameMenuContract
import com.mozhimen.emulatork.ext.input.MenuMgr
import java.security.InvalidParameterException

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

        val game = extras?.getSerializable(GameMenuContract.EXTRA_GAME) as Game?
            ?: throw InvalidParameterException("Missing EXTRA_GAME")

        val systemCoreConfig = extras?.getSerializable(GameMenuContract.EXTRA_SYSTEM_CORE_CONFIG) as GameSystemCoreConfig?
            ?: throw InvalidParameterException("Missing EXTRA_SYSTEM_CORE_CONFIG")

        runOnViewLifecycleState(Lifecycle.State.CREATED) {
            setupSavePreference(game, systemCoreConfig)
        }
    }

    private suspend fun setupSavePreference(game: Game, systemCoreConfig: GameSystemCoreConfig) {
        val slotsInfo = statesManager().getSavedSlotsInfo(game, systemCoreConfig.coreID)

        slotsInfo.forEachIndexed { index, saveInfo ->
            val bitmap = MenuMgr.getSaveStateBitmap(
                requireContext(),
                statesPreviewManager(),
                saveInfo,
                game,
                systemCoreConfig.coreID,
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

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return MenuMgr.onPreferenceTreeClicked(activity, preference)
    }


}
