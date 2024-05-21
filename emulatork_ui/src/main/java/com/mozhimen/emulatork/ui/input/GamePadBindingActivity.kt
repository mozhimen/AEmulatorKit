package com.mozhimen.emulatork.ui.input

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
/**
 * @ClassName GamePadBindingActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GamePadBindingActivity : RetrogradeFragmentActivity() {

    @Inject
    lateinit var inputDeviceManager: com.mozhimen.emulatork.ui.input.InputDeviceManager

    private lateinit var inputBindingUpdater: com.mozhimen.emulatork.ui.input.InputBindingUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inputBindingUpdater = com.mozhimen.emulatork.ui.input.InputBindingUpdater(inputDeviceManager, intent)

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

    @dagger.Module
    abstract class Module
}
