package com.mozhimen.emulatork.test.hilt

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.AppInitializer
import androidx.work.Configuration
import com.google.android.material.color.DynamicColors
import com.mozhimen.basick.elemk.android.app.bases.BaseApplication
import com.mozhimen.basick.lintk.optins.OApiMultiDex_InApplication
import com.mozhimen.basick.utilk.android.content.isMainProcess
import com.mozhimen.basick.utilk.android.util.UtilKLogWrapper
import com.mozhimen.emulatork.basic.android.ContextHandler
import com.mozhimen.emulatork.test.hilt.startup.MainProcessInitializer
import com.mozhimen.emulatork.basic.startup.GameProcessInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
@OptIn(OApiMultiDex_InApplication::class)
class LemuroidApplication : BaseApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        val initializeComponent = if (isMainProcess()) {
            MainProcessInitializer::class.java
        } else {
            GameProcessInitializer::class.java
        }

        AppInitializer.getInstance(this).initializeComponent(initializeComponent)

        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ContextHandler.attachBaseContext(base)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        UtilKLogWrapper.w(TAG, "workerFactory is init " + this::workerFactory.isInitialized)
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
