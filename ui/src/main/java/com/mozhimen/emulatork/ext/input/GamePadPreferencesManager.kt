package com.mozhimen.emulatork.ext.input

import android.content.Context
import android.content.Intent
import android.view.InputDevice
import android.view.KeyEvent
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.key.InputKeyRetro
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.input.utils.getDefaultMenu
import com.mozhimen.emulatork.input.utils.getInputUnit
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.game.pad.AbsGamePadBindingActivity
import java.util.Locale

/**
 * @ClassName GamePadPreferencesHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
open class GamePadPreferencesManager constructor(
    private val inputUnitManager: InputUnitManager,
    private val _gamePadBindingActivityClazz: Class<out AbsGamePadBindingActivity>,
    private val isLeanback: Boolean
) {
    companion object {
        fun displayNameForKeyCode(keyCode: Int): String {
            return when (keyCode) {
                KeyEvent.KEYCODE_BUTTON_THUMBL -> "L3"
                KeyEvent.KEYCODE_BUTTON_THUMBR -> "R3"
                KeyEvent.KEYCODE_BUTTON_MODE -> "Options"
                KeyEvent.KEYCODE_UNKNOWN -> " - "
                else ->
                    KeyEvent.keyCodeToString(keyCode)
                        .split("_")
                        .last()
                        .lowercase()
                        .replaceFirstChar { it.titlecase(Locale.ENGLISH) }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    suspend fun addGamePadsPreferencesToScreen(
        context: Context,
        preferenceScreen: PreferenceScreen,
        gamePads: List<InputDevice>,
        enabledGamePads: List<InputDevice>
    ) {
        val distinctGamePads = getDistinctGamePads(gamePads)
        val distinctEnabledGamePads = getDistinctGamePads(enabledGamePads)

        addEnabledCategory(context, preferenceScreen, distinctGamePads)

        distinctEnabledGamePads
            .forEach {
                addPreferenceCategoryForInputDevice(context, preferenceScreen, it)
            }

        addExtraCategory(context, preferenceScreen)

        refreshGamePadsPreferencesToScreen(preferenceScreen, distinctEnabledGamePads)
    }

    suspend fun refreshGamePadsPreferencesToScreen(
        preferenceScreen: PreferenceScreen,
        enabledGamePads: List<InputDevice>
    ) {
        getDistinctGamePads(enabledGamePads)
            .forEach { refreshPreferenceCategoryForInputDevice(preferenceScreen, it) }
    }

    ////////////////////////////////////////////////////////////////////////////////

    private fun getDistinctGamePads(gamePads: List<InputDevice>): List<InputDevice> {
        return gamePads.distinctBy { it.descriptor }
    }

    private fun addEnabledCategory(
        context: Context,
        preferenceScreen: PreferenceScreen,
        gamePads: List<InputDevice>
    ) {
        if (gamePads.isEmpty())
            return

        val categoryTitle = context.resources.getString(R.string.settings_gamepad_category_enabled)
        val category = createCategory(context, preferenceScreen, categoryTitle)
        preferenceScreen.addPreference(category)

        gamePads.forEach { gamePad ->
            category.addPreference(buildGamePadEnabledPreference(context, gamePad))
        }
    }

    private fun addExtraCategory(context: Context, preferenceScreen: PreferenceScreen) {
        val categoryTitle = context.resources.getString(R.string.settings_gamepad_category_general)
        val category = createCategory(context, preferenceScreen, categoryTitle)

        Preference(context).apply {
            key = context.resources.getString(R.string.pref_key_reset_gamepad_bindings)
            title = context.resources.getString(R.string.settings_gamepad_title_reset_bindings)
            isIconSpaceReserved = false
            category.addPreference(this)
        }
    }

    private fun createCategory(
        context: Context,
        preferenceScreen: PreferenceScreen,
        title: String
    ): PreferenceCategory {
        val category = PreferenceCategory(context)
        preferenceScreen.addPreference(category)
        category.title = title
        category.isIconSpaceReserved = false
        return category
    }

    private fun addPreferenceCategoryForInputDevice(
        context: Context,
        preferenceScreen: PreferenceScreen,
        inputDevice: InputDevice
    ) {
        val category = createCategory(context, preferenceScreen, inputDevice.name)
        preferenceScreen.addPreference(category)

        inputDevice.getInputUnit().getCustomizableKeys()
            .map { buildKeyBindingPreference(context, inputDevice, it) }
            .forEach {
                category.addPreference(it)
            }

        buildGameMenuShortcutPreference(context, inputDevice)?.let {
            category.addPreference(it)
        }
    }

    private suspend fun refreshPreferenceCategoryForInputDevice(
        preferenceScreen: PreferenceScreen,
        inputDevice: InputDevice
    ) {
        val inverseBindings: Map<InputKeyRetro, InputKey> = inputUnitManager.getBindings(inputDevice)
            .map { it.value to it.key }
            .toMap()

        inputDevice.getInputUnit().getCustomizableKeys()
            .forEach { retroKey ->
                val boundKey = inverseBindings[retroKey]?.keyCode ?: KeyEvent.KEYCODE_UNKNOWN
                val preferenceKey = InputUnitManager.computeKeyBindingRetroKeyPreference(inputDevice, retroKey)
                val preference = preferenceScreen.findPreference<Preference>(preferenceKey)
                preference?.summaryProvider = Preference.SummaryProvider<Preference> {
                    displayNameForKeyCode(boundKey)
                }
            }
    }

    private fun buildGamePadEnabledPreference(
        context: Context,
        inputDevice: InputDevice
    ): Preference {
        val preference = SwitchPreference(context)
        preference.key = InputUnitManager.computeEnabledGamePadPreference(inputDevice)
        preference.title = inputDevice.name
        preference.setDefaultValue(inputDevice.getInputUnit().isEnabledByDefault(context))
        preference.isIconSpaceReserved = false
        return preference
    }

    private fun buildKeyBindingPreference(
        context: Context,
        inputDevice: InputDevice,
        inputKeyRetro: InputKeyRetro
    ): Preference {
        val preference = Preference(context)
        preference.key = InputUnitManager.computeKeyBindingRetroKeyPreference(inputDevice, inputKeyRetro)
        preference.title = getRetroPadKeyName(context, inputKeyRetro.keyCode)
        preference.setOnPreferenceClickListener {
            displayChangeDialog(context, inputDevice, inputKeyRetro.keyCode)
            true
        }
        preference.isIconSpaceReserved = false
        return preference
    }

    private fun displayChangeDialog(context: Context, inputDevice: InputDevice, retroKey: Int) {
        val activity = /*if (isLeanback) {
            TVGamePadBindingActivity::class.java
        } else {*/
            _gamePadBindingActivityClazz
//        }

        val intent = Intent(context, activity).apply {
            putExtra(GamePadBindingUpdater.REQUEST_DEVICE, inputDevice)
            putExtra(GamePadBindingUpdater.REQUEST_RETRO_KEY, retroKey)
        }
        context.startActivity(intent)
    }

    private fun buildGameMenuShortcutPreference(
        context: Context,
        inputDevice: InputDevice
    ): Preference? {
        val default = inputDevice.getDefaultMenu() ?: return null
        val supportedShortcuts = inputDevice.getInputUnit().getSupportMenus()

        val preference = ListPreference(context)
        preference.key = InputUnitManager.computeGameMenuShortcutPreference(inputDevice)
        preference.title = context.getString(R.string.settings_gamepad_title_game_menu)
        preference.entries = supportedShortcuts.map { it.name }.toTypedArray()
        preference.entryValues = supportedShortcuts.map { it.name }.toTypedArray()
        preference.setValueIndex(supportedShortcuts.indexOf(default))
        preference.setDefaultValue(default.name)
        preference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        preference.isIconSpaceReserved = false
        return preference
    }

    private fun getRetroPadKeyName(context: Context, key: Int): String {
        return context.resources.getString(
            R.string.settings_retropad_button_name,
            displayNameForKeyCode(key)
        )
    }
}
