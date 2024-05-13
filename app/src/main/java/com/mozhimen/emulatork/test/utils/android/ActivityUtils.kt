package com.mozhimen.emulatork.test.utils.android

import android.app.Activity
import androidx.appcompat.app.AlertDialog

/**
 * @ClassName ActivityUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
fun Activity.displayErrorDialog(messageId: Int, actionLabelId: Int, action: () -> Unit) {
    displayErrorDialog(resources.getString(messageId), resources.getString(actionLabelId), action)
}

fun Activity.displayErrorDialog(message: String, actionLabel: String, action: () -> Unit) {
    AlertDialog.Builder(this)
        .setMessage(message)
        .setPositiveButton(actionLabel) { _, _ -> action() }
        .setCancelable(false)
        .show()
}
