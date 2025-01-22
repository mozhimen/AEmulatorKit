package com.mozhimen.emulatork.common.input

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.system.ESystemType
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.core.type.ECoreType
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadConfig
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

    suspend fun getGamepadConfigs(
        systemType: ESystemType,
        coreBundle: CoreBundle
    ): Map<Int, GamepadConfig> = withContext(Dispatchers.IO) {
        coreBundle.gamepadConfigMap.entries
            .associate { (port, controllers) ->
                val currentName = sharedPreferences.value.getString(
                    getSharedPreferencesId(systemType.simpleName, coreBundle.eCoreType, port),
                    null
                )

                val currentController = controllers
                    .firstOrNull { it.name == currentName } ?: controllers.first()

                port to currentController
            }
    }

    companion object {
        fun getSharedPreferencesId(systemId: String, eCoreType: ECoreType, port: Int) =
            "pref_key_controller_type_${systemId}_${eCoreType.coreName}_$port"
    }
}
