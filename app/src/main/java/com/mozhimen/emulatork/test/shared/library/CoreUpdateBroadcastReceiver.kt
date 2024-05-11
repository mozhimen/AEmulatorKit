package com.mozhimen.emulatork.test.shared.library

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * @ClassName CoreUpdateBroadcastReceiver
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class CoreUpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        LibraryIndexScheduler.cancelCoreUpdate(context!!.applicationContext)
    }
}
