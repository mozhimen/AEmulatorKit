package com.mozhimen.emulatork.common.android

import android.app.Activity

/**
 * @ClassName BusyActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface BusyProvider {
    fun activity(): Activity
    fun isBusy(): Boolean
}
