package com.mozhimen.emulatork.input.unit

import android.content.Context
import android.content.SharedPreferences
import android.hardware.input.InputManager
import android.view.InputDevice
import android.view.KeyEvent
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.mozhimen.basick.utilk.android.hardware.UtilKInputManager
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.key.InputKeyRetro
import com.mozhimen.emulatork.input.utils.findMenuByName
import com.mozhimen.emulatork.input.utils.getDefaultMenu
import com.mozhimen.emulatork.input.utils.inputKeyRetroListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import com.mozhimen.emulatork.input.virtual.menu.Menu
import com.mozhimen.emulatork.input.utils.getInputUnit

/**
 * @ClassName InputDeviceManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
open class InputUnitManager(
    private val context: Context,
    sharedPreferencesFactory: Lazy<SharedPreferences>
) {

    private val inputManager = UtilKInputManager.get(context)

    private val sharedPreferences by lazy { sharedPreferencesFactory.value }

    private val flowSharedPreferences by lazy { FlowSharedPreferences(sharedPreferences) }

    fun getInputBindingsObservable(): Flow<(InputDevice?) -> Map<InputKey, InputKeyRetro>> {
        return getEnabledInputsObservable()
            .map { inputDevices ->
                inputDevices.associateWith { getBindings(it) }
            }
            .map { bindings -> { bindings[it] ?: mapOf() } }
    }

    fun getInputMenuShortCutObservable(): Flow<Menu?> {
        return getEnabledInputsObservable()
            .map { devices ->
                val device = devices.firstOrNull()
                device
                    ?.let {
                        sharedPreferences.getString(
                            computeGameMenuShortcutPreference(it),
                            it.getDefaultMenu()?.name
                        )
                    }
                    ?.let { device.findMenuByName( it) }
            }
    }

    fun getGamePadsPortMapperObservable(): Flow<(InputDevice?) -> Int?> {
        return getEnabledInputsObservable().map { gamePads ->
            val portMappings = gamePads
                .mapIndexed { index, inputDevice -> inputDevice.id to index }
                .toMap()
            return@map { inputDevice -> portMappings[inputDevice?.id] }
        }
    }

    suspend fun getBindings(inputDevice: InputDevice): Map<InputKey, InputKeyRetro> = withContext(Dispatchers.IO) {
        val sharedPreferencesResult = runCatching {
            val string = sharedPreferences.getString(computeKeyBindingGamePadPreference(inputDevice), null)!!
            Json.decodeFromString(bindingsMapSerializer, string)
        }

        sharedPreferencesResult.getOrDefault(getDefaultBinding(inputDevice))
    }

    suspend fun updateBinding(
        inputDevice: InputDevice,
        inputKeyRetro: InputKeyRetro,
        inputKey: InputKey
    ) = withContext(Dispatchers.IO) {

        val prevBindings = getBindings(inputDevice).entries
            .map { it.key to it.value }
            .filter { (_, value) -> value != inputKeyRetro }

        val bindings = prevBindings + listOf(inputKey to inputKeyRetro)

        val sharedPreferencesContent = Json.encodeToString(bindingsMapSerializer, bindings.toMap())

        sharedPreferences.edit()
            .putString(computeKeyBindingGamePadPreference(inputDevice), sharedPreferencesContent)
            .commit()
    }

    suspend fun resetAllBindings() = withContext(Dispatchers.IO) {
        val editor = sharedPreferences.edit()
        sharedPreferences.all.keys
            .filter { it.startsWith(GAME_PAD_BINDING_PREFERENCE_BASE_KEY) }
            .forEach { editor.remove(it) }
        editor.commit()
    }

    fun getGamePadsObservable(): Flow<List<InputDevice>> {
        val result = MutableStateFlow(getAllGamePads())

        val listener = object : InputManager.InputDeviceListener {
            override fun onInputDeviceAdded(deviceId: Int) {
                result.value = getAllGamePads()
            }

            override fun onInputDeviceChanged(deviceId: Int) {
                result.value = getAllGamePads()
            }

            override fun onInputDeviceRemoved(deviceId: Int) {
                result.value = getAllGamePads()
            }
        }

        return result
            .onSubscription { inputManager.registerInputDeviceListener(listener, null) }
            .onCompletion { inputManager.unregisterInputDeviceListener(listener) }
    }

    fun getEnabledInputsObservable(): Flow<List<InputDevice>> {
        return getGamePadsObservable()
            .flatMapLatest { devices ->
                if (devices.isEmpty()) {
                    return@flatMapLatest flowOf(emptyList())
                }

                val deviceStatuesFlows = devices.map { getDeviceStatus(it) }
                combine(deviceStatuesFlows) { deviceStatues ->
                    deviceStatues
                        .filter { it.enabled }
                        .map { it.device }
                }
            }
    }

    private fun getDeviceStatus(inputDevice: InputDevice): Flow<DeviceStatus> {
        val defaultValue = inputDevice.getInputUnit().isEnabledByDefault(context)
        return flowSharedPreferences.getBoolean(computeEnabledGamePadPreference(inputDevice), defaultValue)
            .asFlow()
            .map { isEnabled -> DeviceStatus(inputDevice, isEnabled) }
    }

    private fun getDefaultBinding(inputDevice: InputDevice): Map<InputKey, InputKeyRetro> {
        return inputDevice
            .getInputUnit()
            .getInputKeyMap()
    }

    private fun getAllGamePads(): List<InputDevice> {
        return runCatching {
            InputDevice.getDeviceIds()
                .map { InputDevice.getDevice(it)!! }
                .filter { it.getInputUnit().isSupported() }
                .filter { it.name !in BLACKLISTED_DEVICES }
                .sortedBy { it.controllerNumber }
        }.getOrNull() ?: listOf()
    }

    private data class DeviceStatus(val device: InputDevice, val enabled: Boolean)

    companion object {
        private const val GAME_PAD_BINDING_PREFERENCE_BASE_KEY = "pref_key_gamepad_binding_key"
        private const val GAME_PAD_ENABLED_PREFERENCE_BASE_KEY = "pref_key_gamepad_enabled"

        private val bindingsMapSerializer = MapSerializer(InputKey.serializer(), InputKeyRetro.serializer())

        private fun getSharedPreferencesId(inputDevice: InputDevice) = inputDevice.descriptor

        // This is a last resort, but sadly there are some devices which present keys and the
        // SOURCE_GAMEPAD, so we basically black list them.
        private val BLACKLISTED_DEVICES = setOf(
            "virtual-search"
        )

        fun computeEnabledGamePadPreference(inputDevice: InputDevice) =
            "${GAME_PAD_ENABLED_PREFERENCE_BASE_KEY}_${getSharedPreferencesId(inputDevice)}"

        fun computeGameMenuShortcutPreference(inputDevice: InputDevice) =
            "${GAME_PAD_BINDING_PREFERENCE_BASE_KEY}_${getSharedPreferencesId(inputDevice)}_gamemenu"

        fun computeKeyBindingGamePadPreference(inputDevice: InputDevice) =
            "${GAME_PAD_BINDING_PREFERENCE_BASE_KEY}_${getSharedPreferencesId(inputDevice)}"

        fun computeKeyBindingRetroKeyPreference(
            inputDevice: InputDevice,
            inputKeyRetro: InputKeyRetro
        ): String {
            val keyCode = inputKeyRetro.keyCode
            return "${GAME_PAD_BINDING_PREFERENCE_BASE_KEY}_${getSharedPreferencesId(inputDevice)}_$keyCode"
        }

        val OUTPUT_KEYS: List<InputKeyRetro> = inputKeyRetroListOf(
            KeyEvent.KEYCODE_BUTTON_A,
            KeyEvent.KEYCODE_BUTTON_B,
            KeyEvent.KEYCODE_BUTTON_X,
            KeyEvent.KEYCODE_BUTTON_Y,
            KeyEvent.KEYCODE_BUTTON_START,
            KeyEvent.KEYCODE_BUTTON_SELECT,
            KeyEvent.KEYCODE_BUTTON_L1,
            KeyEvent.KEYCODE_BUTTON_L2,
            KeyEvent.KEYCODE_BUTTON_R1,
            KeyEvent.KEYCODE_BUTTON_R2,
            KeyEvent.KEYCODE_BUTTON_THUMBL,
            KeyEvent.KEYCODE_BUTTON_THUMBR,
            KeyEvent.KEYCODE_BUTTON_MODE,
            KeyEvent.KEYCODE_UNKNOWN,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_RIGHT,
        )
    }
}
