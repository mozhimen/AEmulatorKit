package com.mozhimen.emulatork.test.shared.settings

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.test.shared.library.LibraryIndexScheduler

/**
 * @ClassName CoresSelectionPreferences
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class CoresSelectionPreferences {

    fun addCoresSelectionPreferences(preferenceScreen: PreferenceScreen) {
        val context = preferenceScreen.context

        GameSystem.all()
            .filter { it.systemCoreConfigs.size > 1 }
            .forEach {
                preferenceScreen.addPreference(createPreference(context, it))
            }
    }

    private fun createPreference(context: Context, system: GameSystem): Preference {
        val preference = ListPreference(context)
        preference.key = CoresSelection.computeSystemPreferenceKey(system.id)
        preference.title = context.getString(system.titleResId)
        preference.isIconSpaceReserved = false
        preference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        preference.setDefaultValue(system.systemCoreConfigs.map { it.coreID.coreName }.first())
        preference.isEnabled = system.systemCoreConfigs.size > 1

        preference.entries = system.systemCoreConfigs
            .map { it.coreID.coreDisplayName }
            .toTypedArray()

        preference.entryValues = system.systemCoreConfigs
            .map { it.coreID.coreName }
            .toTypedArray()

        preference.setOnPreferenceChangeListener { _, _ ->
            LibraryIndexScheduler.scheduleCoreUpdate(context.applicationContext)
            true
        }

        return preference
    }
}
