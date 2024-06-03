package com.mozhimen.emulatork.input.virtual.gamepad

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.controller.touch.ControllerTouchConfig
import com.mozhimen.emulatork.core.ECoreId
import com.mozhimen.emulatork.basic.game.system.GameSystemCoreConfig
import com.mozhimen.emulatork.basic.system.ESystemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @ClassName ControllerConfigsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
open class GamepadConfigManager(private val sharedPreferences: Lazy<SharedPreferences>) {

    suspend fun getControllerConfigs(
        systemType: ESystemType,
        systemCoreConfig: GameSystemCoreConfig
    ): Map<Int, ControllerTouchConfig> = withContext(Dispatchers.IO) {
        systemCoreConfig.controllerConfigs.entries
            .associate { (port, controllers) ->
                val currentName = sharedPreferences.value.getString(
                    getSharedPreferencesId(systemType.simpleName, systemCoreConfig.coreID, port),
                    null
                )

                val currentController = controllers
                    .firstOrNull { it.name == currentName } ?: controllers.first()

                port to currentController
            }
    }

    companion object {
        fun getSharedPreferencesId(systemId: String, coreID: com.mozhimen.emulatork.core.ECoreId, port: Int) =
            "pref_key_controller_type_${systemId}_${coreID.coreName}_$port"
    }
}
