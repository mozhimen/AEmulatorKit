package com.mozhimen.emulatork.basic.core.options

import android.content.Context
import com.mozhimen.emulatork.basic.game.system.GameSystemExposedSetting
import java.io.Serializable

/**
 * @ClassName LemuroidCoreOption
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
data class CoreOptionSetting(
    private val gameSystemExposedSetting: GameSystemExposedSetting,
    private val coreOption: CoreOption
) : Serializable {

    fun getKey(): String {
        return gameSystemExposedSetting.key
    }

    fun getDisplayName(context: Context): String {
        return context.getString(gameSystemExposedSetting.titleId)
    }

    fun getEntries(context: Context): List<String> {
        if (gameSystemExposedSetting.values.isEmpty()) {
            return coreOption.optionValues.map { it.capitalize() }
        }

        return getCorrectExposedSettings().map { context.getString(it.titleId) }
    }

    fun getEntriesValues(): List<String> {
        if (gameSystemExposedSetting.values.isEmpty()) {
            return coreOption.optionValues.map { it }
        }

        return getCorrectExposedSettings().map { it.key }
    }

    fun getCurrentValue(): String {
        return coreOption.variable.value
    }

    fun getCurrentIndex(): Int {
        return maxOf(getEntriesValues().indexOf(getCurrentValue()), 0)
    }

    //////////////////////////////////////////////////////////////////

    private fun getCorrectExposedSettings(): List<GameSystemExposedSetting.Value> {
        return gameSystemExposedSetting.values
            .filter { it.key in coreOption.optionValues }
    }
}
