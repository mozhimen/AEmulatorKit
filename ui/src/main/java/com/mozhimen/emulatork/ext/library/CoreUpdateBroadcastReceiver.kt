package com.mozhimen.emulatork.ext.library

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mozhimen.emulatork.ext.works.WorkScheduler

/**
 * @ClassName CoreUpdateBroadcastReceiver
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
 class CoreUpdateBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        WorkScheduler.cancelCoreUpdate(context!!)
    }
}
