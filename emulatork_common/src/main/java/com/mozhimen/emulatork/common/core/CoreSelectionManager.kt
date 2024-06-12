package com.mozhimen.emulatork.common.core

import android.content.SharedPreferences
import com.mozhimen.emulatork.common.system.SystemBundle
import com.mozhimen.emulatork.basic.system.ESystemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @ClassName CoresSelection
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
open class CoreSelectionManager(private val sharedPreferences: Lazy<SharedPreferences>) {

    companion object {
        private const val CORE_SELECTION_BINDING_PREFERENCE_BASE_KEY = "pref_key_core_selection"

        fun computeSystemPreferenceKey(eSystemType: ESystemType) =
            "${CORE_SELECTION_BINDING_PREFERENCE_BASE_KEY}_${eSystemType.simpleName}"
    }

    ////////////////////////////////////////////////////////////////////////

    suspend fun getCoreConfigForSystem(system: SystemBundle) = withContext(Dispatchers.IO) {
        fetchSystemCoreConfig(system)
    }

    ////////////////////////////////////////////////////////////////////////

    private fun fetchSystemCoreConfig(system: SystemBundle): CoreBundle {
        val setting = sharedPreferences.value
            .getString(computeSystemPreferenceKey(system.eSystemType), null)

        return system.coreBundles.firstOrNull { it?.eCoreType?.coreName == setting }
            ?: system.coreBundles.first()
    }
}
