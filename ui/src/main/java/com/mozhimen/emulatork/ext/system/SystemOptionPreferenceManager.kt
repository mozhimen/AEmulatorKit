package com.mozhimen.emulatork.ext.system

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreference
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.common.core.CorePropertyManager
import com.mozhimen.emulatork.common.input.GamepadConfigManager
import com.mozhimen.emulatork.common.system.SystemOption
import com.mozhimen.emulatork.core.type.ECoreType
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadConfig

/**
 * @ClassName CoreOptionsPreferenceHelper
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object SystemOptionPreferenceManager {

    private val BOOLEAN_SET = setOf("enabled", "disabled")

    fun addPreferences(
        preferenceScreen: PreferenceScreen,
        systemID: String,
        systemOptions: List<SystemOption>,
        advancedOptions: List<SystemOption>
    ) {
        if (systemOptions.isEmpty() && advancedOptions.isEmpty()) {
            return
        }

        val context = preferenceScreen.context

        val title = context.getString(R.string.core_settings_category_preferences)
        val preferencesCategory = createCategory(preferenceScreen.context, preferenceScreen, title)

        addPreferences(context, preferencesCategory, systemOptions, systemID)
        addPreferences(context, preferencesCategory, advancedOptions, systemID)
    }

    fun addControllers(
        preferenceScreen: PreferenceScreen,
        systemID: String,
        eCoreType: ECoreType,
        connectedGamePads: Int,
        gamepadConfigMap: Map<Int, List<GamepadConfig>>
    ) {
        val visibleControllers = (0 until connectedGamePads)
            .map { it to gamepadConfigMap[it] }
            .filter { (_, controllers) -> controllers != null && controllers.size >= 2 }

        if (visibleControllers.isEmpty())
            return

        val context = preferenceScreen.context
        val title = context.getString(R.string.core_settings_category_controllers)
        val category = createCategory(context, preferenceScreen, title)

        visibleControllers
            .forEach { (port, controllers) ->
                val preference = buildControllerPreference(context, systemID, eCoreType, port, controllers!!)
                category.addPreference(preference)
            }
    }

    private fun addPreferences(
        context: Context,
        preferenceGroup: PreferenceGroup,
        systemOptions: List<SystemOption>,
        systemID: String
    ) {
        systemOptions
            .map { convertToPreference(context, it, systemID) }
            .forEach { preferenceGroup.addPreference(it) }
    }

    private fun convertToPreference(
        context: Context,
        systemOption: SystemOption,
        systemID: String
    ): Preference {
        return if (systemOption.getEntriesValues().toSet() == BOOLEAN_SET) {
            buildSwitchPreference(context, systemOption, systemID)
        } else {
            buildListPreference(context, systemOption, systemID)
        }
    }

    private fun buildListPreference(
        context: Context,
        systemOption: SystemOption,
        systemID: String
    ): ListPreference {
        val preference = ListPreference(context)
        preference.key = CorePropertyManager.computeSharedPreferenceKey(systemOption.getKey(), systemID)
        preference.title = systemOption.getDisplayName(context)
        preference.entries = systemOption.getEntries(context).toTypedArray()
        preference.entryValues = systemOption.getEntriesValues().toTypedArray()
        preference.setDefaultValue(systemOption.getCurrentValue())
        preference.setValueIndex(systemOption.getCurrentIndex())
        preference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        preference.isIconSpaceReserved = false
        return preference
    }

    private fun buildSwitchPreference(
        context: Context,
        systemOption: SystemOption,
        systemID: String
    ): SwitchPreference {
        val preference = SwitchPreference(context)
        preference.key = CorePropertyManager.computeSharedPreferenceKey(systemOption.getKey(), systemID)
        preference.title = systemOption.getDisplayName(context)
        preference.setDefaultValue(systemOption.getCurrentValue() == "enabled")
        preference.isChecked = systemOption.getCurrentValue() == "enabled"
        preference.isIconSpaceReserved = false
        return preference
    }

    private fun buildControllerPreference(
        context: Context,
        systemID: String,
        eCoreType: ECoreType,
        port: Int,
        gamepadConfigs: List<GamepadConfig>
    ): Preference {
        val preference = ListPreference(context)
        preference.key = GamepadConfigManager.getSharedPreferencesId(systemID, eCoreType, port)
        preference.title = context.getString(R.string.core_settings_controller, (port + 1).toString())
        preference.entries = gamepadConfigs.map { context.getString(it.displayName) }.toTypedArray()
        preference.entryValues = gamepadConfigs.map { it.name }.toTypedArray()
        preference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        preference.isIconSpaceReserved = false
        preference.setDefaultValue(gamepadConfigs.map { it.name }.first())
        return preference
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
}
