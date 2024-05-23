package com.mozhimen.emulatork.basic.controller

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.controller.touch.ControllerTouchConfig
import com.mozhimen.emulatork.basic.core.CoreID
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.game.system.GameSystemID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @ClassName ControllerConfigsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
open class ControllerConfigsManager(private val sharedPreferences: Lazy<SharedPreferences>) {

    suspend fun getControllerConfigs(
        systemId: GameSystemID,
        systemCoreConfig: GameSystemCoreConfig
    ): Map<Int, ControllerTouchConfig> = withContext(Dispatchers.IO) {
        systemCoreConfig.controllerConfigs.entries
            .associate { (port, controllers) ->
                val currentName = sharedPreferences.value.getString(
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
