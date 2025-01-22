package com.mozhimen.emulatork.common.core

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.system.ESystemType
import com.mozhimen.emulatork.core.property.CoreProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.InvalidParameterException

/**
 * @ClassName CoreVariablesManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/20
 * @Version 1.0
 */
open class CorePropertyManager(
    private val sharedPreferences: Lazy<SharedPreferences>
) {

    companion object {
        private const val RETRO_OPTION_PREFIX = "cv"

        fun computeSharedPreferenceKey(retroVariableName: String, systemID: String): String {
            return "${computeSharedPreferencesPrefix(systemID)}$retroVariableName"
        }

        fun computeOriginalKey(sharedPreferencesKey: String, systemID: String): String {
            return sharedPreferencesKey.replace(computeSharedPreferencesPrefix(systemID), "")
        }

        private fun computeSharedPreferencesPrefix(systemID: String): String {
            return "${RETRO_OPTION_PREFIX}_${systemID}_"
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    suspend fun getOptionsForCore(eSystemType: ESystemType, coreBundle: CoreBundle): List<CoreProperty> {
        val defaultMap = convertCoreVariablesToMap(coreBundle.coreProperties)
        val coreVariables = retrieveCustomCoreVariables(eSystemType, coreBundle)
        val coreVariablesMap = defaultMap + convertCoreVariablesToMap(coreVariables)
        return convertMapToCoreVariables(coreVariablesMap)
    }

    //////////////////////////////////////////////////////////////////////////////

    private fun convertMapToCoreVariables(variablesMap: Map<String, String>): List<CoreProperty> {
        return variablesMap.entries.map { CoreProperty(it.key, it.value) }
    }

    private fun convertCoreVariablesToMap(coreProperties: List<CoreProperty>): Map<String, String> {
        return coreProperties.associate { it.key to it.value }
    }

    private suspend fun retrieveCustomCoreVariables(eSystemType: ESystemType, coreBundle: CoreBundle): List<CoreProperty> =
        withContext(Dispatchers.IO) {
            val exposedKeys = coreBundle.systemSettings_exposed
            val exposedAdvancedKeys = coreBundle.systemSettings_exposedAdvanced

            val requestedKeys = (exposedKeys + exposedAdvancedKeys).map { it.key }
                .map { computeSharedPreferenceKey(it, eSystemType.simpleName) }

            sharedPreferences.value.all.filter { it.key in requestedKeys }
                .map { (key, value) ->
                    val result = when (value!!) {
                        is Boolean -> if (value as Boolean) "enabled" else "disabled"
                        is String -> value as String
                        else -> throw InvalidParameterException("Invalid setting in SharedPreferences")
                    }
                    CoreProperty(computeOriginalKey(key, eSystemType.simpleName), result)
                }
        }
}
