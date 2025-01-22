package com.mozhimen.emulatork.basic.startup

import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import com.mozhimen.emulatork.basic.BuildConfig

/**
 * @ClassName DebugInitializer
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class DebugInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
//             com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.plant( com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.DebugTree())
            enableStrictMode()
        }
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
