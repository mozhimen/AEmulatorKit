package com.mozhimen.emulatork.common.system

import android.content.Context
import com.mozhimen.emulatork.basic.system.SystemProperty
import com.mozhimen.emulatork.basic.system.SystemSetting
import com.mozhimen.emulatork.core.option.CoreOption
import java.io.Serializable

/**
 * @ClassName LemuroidCoreOption
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
data class SystemOption(
    private val systemSetting: SystemSetting,
    private val coreOption: CoreOption
) : Serializable {

    fun getKey(): String {
        return systemSetting.key
    }

    fun getDisplayName(context: Context): String {
        return context.getString(systemSetting.titleId)
    }

    fun getEntries(context: Context): List<String> {
        if (systemSetting.values.isEmpty()) {
            return coreOption.optionValues.map { it.capitalize() }
        }

        return getCorrectExposedSettings().map { context.getString(it.titleId) }
    }

    fun getEntriesValues(): List<String> {
        if (systemSetting.values.isEmpty()) {
            return coreOption.optionValues.map { it }
        }

        return getCorrectExposedSettings().map { it.key }
    }

    fun getCurrentValue(): String {
        return coreOption.coreProperty.value
    }

    fun getCurrentIndex(): Int {
        return maxOf(getEntriesValues().indexOf(getCurrentValue()), 0)
    }

    //////////////////////////////////////////////////////////////////

    private fun getCorrectExposedSettings(): List<SystemProperty> {
        return systemSetting.values
            .filter { it.key in coreOption.optionValues }
    }
}
