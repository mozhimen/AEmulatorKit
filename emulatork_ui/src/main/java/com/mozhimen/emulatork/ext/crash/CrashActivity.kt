package com.mozhimen.emulatork.ext.crash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.isVisible
import com.mozhimen.emulatork.ui.R

/**
 * @ClassName GameCrashActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val messageDetail = intent.getStringExtra(EXTRA_MESSAGE_DETAIL)

        findViewById<TextView>(R.id.text1).apply {
            isVisible = !message.isNullOrEmpty()
            text = message
        }

        findViewById<TextView>(R.id.text2).apply {
            isVisible = !messageDetail.isNullOrEmpty()
            text = messageDetail
        }
    }

    companion object {
        private const val EXTRA_MESSAGE = "EXTRA_MESSAGE"
        private const val EXTRA_MESSAGE_DETAIL = "EXTRA_MESSAGE_DETAIL"

        fun launch(activity: Activity, message: String, messageDetail: String?) {
            val intent = Intent(activity, CrashActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, message)
                putExtra(EXTRA_MESSAGE_DETAIL, messageDetail)
            }
            activity.startActivity(intent)
        }
    }
}
