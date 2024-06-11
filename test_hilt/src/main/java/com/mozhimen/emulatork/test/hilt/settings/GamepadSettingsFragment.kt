package com.mozhimen.emulatork.test.hilt.settings

import android.os.Bundle
import android.view.InputDevice
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mozhimen.basick.utilk.androidx.fragment.runOnViewLifecycleState
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.ext.input.GamePadPreferencesManager
import com.mozhimen.basick.elemk.mos.NTuple2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @ClassName GamepadSettingsFragment
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
@AndroidEntryPoint
class GamepadSettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var gamePadPreferencesManager: GamePadPreferencesManager

    @Inject
    lateinit var inputUnitManager: InputUnitManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore =
            SharedPreferencesManager.getSharedPreferencesDataStore(requireContext())
        setPreferencesFromResource(R.xml.empty_preference_screen, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runOnViewLifecycleState(Lifecycle.State.CREATED) {
            val gamePadStatus = combine(
                inputUnitManager.getGamePadsObservable(),
                inputUnitManager.getEnabledInputsObservable(),
                ::NTuple2
            )

            gamePadStatus
                .distinctUntilChanged()
                .collect { (pads, enabledPads) -> addGamePads(pads, enabledPads) }
        }

        runOnViewLifecycleState(Lifecycle.State.RESUMED) {
            inputUnitManager.getEnabledInputsObservable()
                .distinctUntilChanged()
                .collect { refreshGamePads(it) }
        }
    }

    private fun addGamePads(pads: List<InputDevice>, enabledPads: List<InputDevice>) {
        lifecycleScope.launch {
            preferenceScreen.removeAll()
            gamePadPreferencesManager.addGamePadsPreferencesToScreen(
                requireActivity(),
                preferenceScreen,
                pads,
                enabledPads
            )
        }
    }

    private fun refreshGamePads(enabledGamePads: List<InputDevice>) {
        lifecycleScope.launch {
            gamePadPreferencesManager.refreshGamePadsPreferencesToScreen(preferenceScreen, enabledGamePads)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.pref_key_reset_gamepad_bindings) -> lifecycleScope.launch {
                handleResetBindings()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private suspend fun handleResetBindings() {
        inputUnitManager.resetAllBindings()
        addGamePads(
            inputUnitManager.getGamePadsObservable().first(),
            inputUnitManager.getEnabledInputsObservable().first()
        )
    }

//    @dagger.Module
//    class Module
}
