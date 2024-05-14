package com.mozhimen.emulatork.test.shared.settings

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.controller.ControllerConfig
import com.mozhimen.emulatork.basic.library.CoreID
import com.mozhimen.emulatork.basic.library.SystemCoreConfig
import com.mozhimen.emulatork.basic.library.SystemID
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @ClassName ControllerConfigsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class ControllerConfigsManager(private val sharedPreferences: Lazy<SharedPreferences>) {

    suspend fun getControllerConfigs(
        systemId: SystemID,
        systemCoreConfig: SystemCoreConfig
    ): Map<Int, ControllerConfig> = withContext(Dispatchers.IO) {
        systemCoreConfig.controllerConfigs.entries
            .associate { (port, controllers) ->
                val currentName = sharedPreferences.get().getString(
                    getSharedPreferencesId(systemId.dbname, systemCoreConfig.coreID, port),
                    null
                )

                val currentController = controllers
                    .firstOrNull { it.name == currentName } ?: controllers.first()

                port to currentController
            }
    }

    companion object {
        fun getSharedPreferencesId(systemId: String, coreID: CoreID, port: Int) =
            "pref_key_controller_type_${systemId}_${coreID.coreName}_$port"
    }
}
