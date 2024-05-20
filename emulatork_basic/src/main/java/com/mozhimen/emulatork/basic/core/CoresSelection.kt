package com.mozhimen.emulatork.basic.core

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.SystemCoreConfig
import com.mozhimen.emulatork.basic.library.SystemID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @ClassName CoresSelection
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class CoresSelection(private val sharedPreferences: Lazy<SharedPreferences>) {

    suspend fun getCoreConfigForSystem(system: GameSystem) = withContext(Dispatchers.IO) {
        fetchSystemCoreConfig(system)
    }

    private fun fetchSystemCoreConfig(system: GameSystem): SystemCoreConfig {
        val setting = sharedPreferences.value
            .getString(computeSystemPreferenceKey(system.id), null)

        return system.systemCoreConfigs.firstOrNull { it.coreID.coreName == setting }
            ?: system.systemCoreConfigs.first()
    }

    companion object {
        private const val CORE_SELECTION_BINDING_PREFERENCE_BASE_KEY = "pref_key_core_selection"

        fun computeSystemPreferenceKey(systemID: SystemID) =
            "${CORE_SELECTION_BINDING_PREFERENCE_BASE_KEY}_${systemID.dbname}"
    }
}
