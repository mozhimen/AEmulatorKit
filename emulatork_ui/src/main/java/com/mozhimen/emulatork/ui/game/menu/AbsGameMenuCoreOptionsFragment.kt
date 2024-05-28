package com.mozhimen.emulatork.ui.game.menu

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesMgr
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.basic.core.options.CoreOptionSetting
import com.mozhimen.emulatork.ui.R
import java.security.InvalidParameterException
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.ext.core.CoreOptionsPreferenceManager
import com.mozhimen.emulatork.basic.game.menu.GameMenuContract

/**
 * @ClassName GameMenuCoreOptionsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsGameMenuCoreOptionsFragment : PreferenceFragmentCompat() {

//    @Inject
//    lateinit var inputDeviceManager: InputDeviceManager
    abstract fun inputDeviceManager(): InputUnitManager

//    override fun onAttach(context: Context) {
//        AndroidSupportInjection.inject(this)
//        super.onAttach(context)
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesMgr.getSharedPreferencesDataStore(requireContext())
        addPreferencesFromResource(R.xml.empty_preference_screen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runOnViewLifecycleState(Lifecycle.State.CREATED) {
            inputDeviceManager()
                .getGamePadsObservable()
                .collect { updateScreen(it.size) }
        }
    }

    private fun updateScreen(connectedGamePads: Int) {
        preferenceScreen.removeAll()

        val extras = activity?.intent?.extras

        val coreOptions = extras?.getSerializable(GameMenuContract.EXTRA_CORE_OPTIONS) as Array<CoreOptionSetting>?
            ?: throw InvalidParameterException("Missing EXTRA_CORE_OPTIONS")

        val advancedCoreOptions = extras?.getSerializable(
            GameMenuContract.EXTRA_ADVANCED_CORE_OPTIONS
        ) as Array<CoreOptionSetting>?
            ?: throw InvalidParameterException("Missing EXTRA_ADVANCED_CORE_OPTIONS")

        val game = extras?.getSerializable(GameMenuContract.EXTRA_GAME) as Game?
            ?: throw InvalidParameterException("Missing EXTRA_GAME")

        val coreConfig = extras?.getSerializable(GameMenuContract.EXTRA_SYSTEM_CORE_CONFIG) as GameSystemCoreConfig?
            ?: throw InvalidParameterException("Missing EXTRA_SYSTEM_CORE_CONFIG")

        CoreOptionsPreferenceManager.addPreferences(
            preferenceScreen,
            game.systemId,
            coreOptions.toList(),
            advancedCoreOptions.toList()
        )

        CoreOptionsPreferenceManager.addControllers(
            preferenceScreen,
            game.systemId,
            coreConfig.coreID,
            maxOf(1, connectedGamePads),
            coreConfig.controllerConfigs
        )
    }
}
