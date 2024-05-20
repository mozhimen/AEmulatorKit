package com.mozhimen.emulatork.basic.core

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.library.SystemCoreConfig
import com.mozhimen.emulatork.basic.library.SystemID
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
open class CoreVariablesManager(private val sharedPreferences: Lazy<SharedPreferences>) {

    suspend fun getOptionsForCore(
        systemID: SystemID,
        systemCoreConfig: SystemCoreConfig
    ): List<CoreVariable> {
        val defaultMap = convertCoreVariablesToMap(systemCoreConfig.defaultSettings)
        val coreVariables = retrieveCustomCoreVariables(systemID, systemCoreConfig)
        val coreVariablesMap = defaultMap + convertCoreVariablesToMap(coreVariables)
        return convertMapToCoreVariables(coreVariablesMap)
    }

    private fun convertMapToCoreVariables(variablesMap: Map<String, String>): List<CoreVariable> {
        return variablesMap.entries.map { CoreVariable(it.key, it.value) }
    }

    private fun convertCoreVariablesToMap(coreVariables: List<CoreVariable>): Map<String, String> {
        return coreVariables.associate { it.key to it.value }
    }

    private suspend fun retrieveCustomCoreVariables(
        systemID: SystemID,
        systemCoreConfig: SystemCoreConfig
    ): List<CoreVariable> = withContext(Dispatchers.IO) {

        val exposedKeys = systemCoreConfig.exposedSettings
        val exposedAdvancedKeys = systemCoreConfig.exposedAdvancedSettings

        val requestedKeys = (exposedKeys + exposedAdvancedKeys).map { it.key }
            .map { computeSharedPreferenceKey(it, systemID.dbname) }

        sharedPreferences.value.all.filter { it.key in requestedKeys }
            .map { (key, value) ->
                val result = when (value!!) {
                    is Boolean -> if (value as Boolean) "enabled" else "disabled"
                    is String -> value as String
                    else -> throw InvalidParameterException("Invalid setting in SharedPreferences")
                }
                CoreVariable(computeOriginalKey(key, systemID.dbname), result)
            }
    }

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
}
