package com.mozhimen.emulatork.ui.game.menu

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.common.system.SystemOption
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.ui.R
import java.security.InvalidParameterException
import com.mozhimen.emulatork.ext.system.SystemOptionPreferenceManager
import com.mozhimen.emulatork.input.virtual.menu.MenuContract

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
            SharedPreferencesManager.getSharedPreferencesDataStore(requireContext())
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

        val coreOptions = extras?.getSerializable(MenuContract.EXTRA_SYSTEM_OPTIONS) as? Array<SystemOption>?
            ?: throw InvalidParameterException("Missing EXTRA_CORE_OPTIONS")

        val advancedCoreOptions = extras?.getSerializable(MenuContract.EXTRA_SYSTEM_OPTIONS_ADVANCED) as? Array<SystemOption>?
            ?: throw InvalidParameterException("Missing EXTRA_ADVANCED_CORE_OPTIONS")

        val game = extras?.getSerializable(MenuContract.EXTRA_GAME) as Game?
            ?: throw InvalidParameterException("Missing EXTRA_GAME")

        val coreConfig = extras?.getSerializable(MenuContract.EXTRA_SYSTEM_CORE_BUNDLE) as CoreBundle?
            ?: throw InvalidParameterException("Missing EXTRA_SYSTEM_CORE_CONFIG")

        SystemOptionPreferenceManager.addPreferences(
            preferenceScreen,
            game.systemName,
            coreOptions.toList(),
            advancedCoreOptions.toList()
        )

        SystemOptionPreferenceManager.addControllers(
            preferenceScreen,
            game.systemName,
            coreConfig.eCoreType,
            maxOf(1, connectedGamePads),
            coreConfig.gamepadConfigMap
        )
    }
}
