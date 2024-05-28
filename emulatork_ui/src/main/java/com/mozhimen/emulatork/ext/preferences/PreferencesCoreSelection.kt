package com.mozhimen.emulatork.ext.preferences

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.core.CoreSelection
import com.mozhimen.emulatork.basic.game.system.GameSystem
import com.mozhimen.emulatork.basic.game.system.GameSystems
import com.mozhimen.emulatork.ext.works.WorkScheduler
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate

/**
 * @ClassName CoresSelectionPreferences
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class PreferencesCoreSelection : IUtilK {

    fun addCoresSelectionPreferences(preferenceScreen: PreferenceScreen, workCoreUpdateClazz: Class<out AbsWorkCoreUpdate>) {
        val context = preferenceScreen.context

        GameSystems.all()
            .filter { it.systemCoreConfigs.size > 1 }
            .forEach {
                preferenceScreen.addPreference(createPreference(context, it, workCoreUpdateClazz))
            }
    }

    private fun createPreference(context: Context, system: GameSystem, workCoreUpdateClazz: Class<out AbsWorkCoreUpdate>): Preference {
        val preference = ListPreference(context)
        preference.key = CoreSelection.computeSystemPreferenceKey(system.id)
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
            WorkScheduler.scheduleCoreUpdate(workCoreUpdateClazz, context)
            true
        }

        return preference
    }
}
