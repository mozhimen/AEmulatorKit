package com.mozhimen.emulatork.ext.preferences

import android.content.Context
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceScreen
import com.mozhimen.emulatork.basic.bios.Bios
import com.mozhimen.emulatork.common.bios.BiosManager
import com.mozhimen.emulatork.ui.R

/**
 * @ClassName BiosPreferences
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class PreferencesBios(private val biosManager: BiosManager) {

    fun addBiosPreferences(preferenceScreen: PreferenceScreen) {
        val context = preferenceScreen.context
        val (installedBios, notInstalledBios) = biosManager.getBiosInfo()

        val detectedBios = createCategory(context, context.getString(R.string.settings_bios_category_detected))
        preferenceScreen.addPreference(detectedBios)
        detectedBios.isVisible = installedBios.isNotEmpty()

        val notDetectedBios = createCategory(context, context.getString(R.string.settings_bios_category_not_detected))
        preferenceScreen.addPreference(notDetectedBios)
        notDetectedBios.isVisible = notInstalledBios.isNotEmpty()

        installedBios.forEach {
            val preference = createBiosPreference(preferenceScreen.context, it)
            preference.isEnabled = true
            detectedBios.addPreference(preference)
        }

        notInstalledBios.forEach {
            val preference = createBiosPreference(preferenceScreen.context, it)
            preference.isEnabled = false
            notDetectedBios.addPreference(preference)
        }
    }

    private fun createBiosPreference(context: Context, bios: Bios): Preference {
        val preference = Preference(context)
        preference.title = bios.description
        preference.summary = bios.displayName()
        preference.isIconSpaceReserved = false
        return preference
    }

    private fun createCategory(context: Context, title: String): PreferenceCategory {
        val category = PreferenceCategory(context)
        category.title = title
        category.isIconSpaceReserved = false
        return category
    }
}
