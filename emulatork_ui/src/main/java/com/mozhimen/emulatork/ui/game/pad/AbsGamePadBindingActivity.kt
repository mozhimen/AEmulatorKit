package com.mozhimen.emulatork.ui.game.pad

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.mozhimen.emulatork.ext.game.pad.GamePadBindingUpdater
import com.mozhimen.emulatork.input.device.InputDeviceManager

/**
 * @ClassName GamePadBindingActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsGamePadBindingActivity : FragmentActivity() {

//    @Inject
//    lateinit var inputDeviceManager: InputDeviceManager
    abstract fun inputDeviceManager(): InputDeviceManager

    private lateinit var gamePadBindingUpdater: GamePadBindingUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gamePadBindingUpdater = GamePadBindingUpdater(inputDeviceManager(), intent)

        if (savedInstanceState == null) {
            AlertDialog.Builder(this)
                .setTitle(gamePadBindingUpdater.getTitle(applicationContext))
                .setMessage(gamePadBindingUpdater.getMessage(applicationContext))
                .setOnKeyListener { _, _, event -> handleKeyEvent(event) }
                .setOnDismissListener { finish() }
                .show()
        }
    }

    private fun handleKeyEvent(event: KeyEvent): Boolean {
        val result = gamePadBindingUpdater.handleKeyEvent(event)

        if (event.action == KeyEvent.ACTION_UP && result) {
            finish()
        }
        return result
    }
}
