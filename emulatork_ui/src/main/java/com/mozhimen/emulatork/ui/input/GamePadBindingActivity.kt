package com.mozhimen.emulatork.ui.input

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

/**
 * @ClassName GamePadBindingActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class GamePadBindingActivity : FragmentActivity() {

//    @Inject
//    lateinit var inputDeviceManager: InputDeviceManager
    abstract fun inputDeviceManager():InputDeviceManager

    private lateinit var inputBindingUpdater: InputBindingUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inputBindingUpdater = InputBindingUpdater(inputDeviceManager(), intent)

        if (savedInstanceState == null) {
            AlertDialog.Builder(this)
                .setTitle(inputBindingUpdater.getTitle(applicationContext))
                .setMessage(inputBindingUpdater.getMessage(applicationContext))
                .setOnKeyListener { _, _, event -> handleKeyEvent(event) }
                .setOnDismissListener { finish() }
                .show()
        }
    }

    private fun handleKeyEvent(event: KeyEvent): Boolean {
        val result = inputBindingUpdater.handleKeyEvent(event)

        if (event.action == KeyEvent.ACTION_UP && result) {
            finish()
        }

        return result
    }


}
