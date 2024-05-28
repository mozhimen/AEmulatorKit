package com.mozhimen.emulatork.ext.game.pad

import android.content.Context
import android.content.Intent
import android.view.InputDevice
import android.view.KeyEvent
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.input.key.InputKeyRetro
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.ui.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * @ClassName InputBindingUpdater
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@OptIn(DelicateCoroutinesApi::class)
class GamePadBindingUpdater(private val inputUnitManager: InputUnitManager, intent: Intent) {

    val extras = parseExtras(intent)

    fun getTitle(context: Context): String {
        val keyName = GamePadPreferencesManager.displayNameForKeyCode(extras.retroKey)
        return context.getString(R.string.gamepad_binding_update_title, keyName)
    }

    fun getMessage(context: Context): String {
        return context.getString(R.string.gamepad_binding_update_description, extras.device.name)
    }

    fun handleKeyEvent(event: KeyEvent): Boolean {
        Timber.d("Received input binding event: $event ${event.device}")
        return when (event.action) {
            KeyEvent.ACTION_DOWN -> onKeyDown(event)
            KeyEvent.ACTION_UP -> onKeyUp(event)
            else -> false
        }
    }

    private fun onKeyDown(event: KeyEvent): Boolean {
        return isTargetedDevice(event.device)
    }

    private fun onKeyUp(event: KeyEvent): Boolean {
        if (!isTargetedDevice(event.device)) return false

        runBlocking {
            GlobalScope.async {
                inputUnitManager.updateBinding(event.device, InputKeyRetro(extras.retroKey), InputKey(event.keyCode))
            }
        }

        return true
    }

    private fun isTargetedDevice(device: InputDevice?): Boolean {
        return device != null && extras.device.name == device.name
    }

    private fun parseExtras(intent: Intent): IntentExtras {
        val device = intent.extras?.getParcelable<InputDevice>(REQUEST_DEVICE)
            ?: throw IllegalArgumentException("REQUEST_DEVICE has not been passed")

        val retroKey = intent.extras?.getInt(REQUEST_RETRO_KEY)
            ?: throw IllegalArgumentException("REQUEST_RETRO_KEY has not been passed")

        return IntentExtras(device, retroKey)
    }

    data class IntentExtras(val device: InputDevice, val retroKey: Int)

    companion object {
        const val REQUEST_DEVICE = "REQUEST_DEVICE"
        const val REQUEST_RETRO_KEY = "REQUEST_RETRO_KEY"
    }
}
