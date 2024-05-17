package com.mozhimen.emulatork.ui.dagger.shared.library

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * @ClassName LibraryIndexBroadcastReceiver
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class LibraryIndexBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        LibraryIndexScheduler.cancelLibrarySync(context!!.applicationContext)
    }
}
