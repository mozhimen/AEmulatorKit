package com.mozhimen.emulatork.ext.preferences

import android.content.Context
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.common.core.CoreSelectionManager
import com.mozhimen.emulatork.common.system.SystemProvider
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

        SystemProvider.getSystems()
            .filter { it.coreBundles.size > 1 }
            .forEach {
                preferenceScreen.addPreference(createPreference(context, it, workCoreUpdateClazz))
            }
    }

    private fun createPreference(context: Context, systemBundle: com.mozhimen.emulatork.common.system.SystemBundle, workCoreUpdateClazz: Class<out AbsWorkCoreUpdate>): Preference {
        val preference = ListPreference(context)
        preference.key = CoreSelectionManager.computeSystemPreferenceKey(systemBundle.eSystemType)
        preference.title = context.getString(systemBundle.titleResId)
        preference.isIconSpaceReserved = false
        preference.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        preference.setDefaultValue(systemBundle.coreBundles.map { it.eCoreType.coreName }.first())
        preference.isEnabled = systemBundle.coreBundles.size > 1

        preference.entries = systemBundle.coreBundles
            .map { it.eCoreType.coreNameDisplay }
            .toTypedArray()

        preference.entryValues = systemBundle.coreBundles
            .map { it.eCoreType.coreName }
            .toTypedArray()

        preference.setOnPreferenceChangeListener { _, _ ->
            WorkScheduler.scheduleCoreUpdate(workCoreUpdateClazz, context)
            true
        }

        return preference
    }
}
