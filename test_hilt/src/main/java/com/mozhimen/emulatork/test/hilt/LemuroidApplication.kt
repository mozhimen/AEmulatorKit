package com.mozhimen.emulatork.test.hilt

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.AppInitializer
import com.google.android.material.color.DynamicColors
import com.mozhimen.basick.elemk.android.app.bases.BaseApplication
import com.mozhimen.basick.lintk.optins.OApiMultiDex_InApplication
import com.mozhimen.basick.utilk.android.content.isMainProcess
import com.mozhimen.emulatork.ext.context.ContextHandler
import com.mozhimen.emulatork.ui.startup.MainProcessInitializer
import com.mozhimen.emulatork.ui.startup.GameProcessInitializer
import dagger.hilt.android.HiltAndroidApp

@OptIn(OApiMultiDex_InApplication::class)
@HiltAndroidApp
class LemuroidApplication : BaseApplication() {

    @OApiMultiDex_InApplication
    @SuppressLint("CheckResult")
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
}