package com.mozhimen.emulatork.basic.game

import android.app.Activity

/**
 * @ClassName BusyActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
interface GameBusyActivity {
    fun activity(): Activity
    fun isBusy(): Boolean
}
