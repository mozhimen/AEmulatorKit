package com.mozhimen.emulatork.basic.startup

import android.content.Context
import androidx.startup.Initializer
import com.mozhimen.basick.utilk.commons.IUtilK


/**
 * @ClassName GameProcessInitializer
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameProcessInitializer : Initializer<Unit>,IUtilK {
    override fun create(context: Context) {
         com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.i(TAG,"Requested initialization of game process tasks")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(DebugInitializer::class.java)
    }
}
