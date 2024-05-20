package com.mozhimen.emulatork.test

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.AppInitializer
import androidx.work.ListenableWorker
import com.google.android.material.color.DynamicColors
import com.mozhimen.basick.utilk.android.content.isMainProcess
import com.mozhimen.emulatork.basic.dagger.interfaces.HasWorkerInjector
import com.mozhimen.emulatork.ext.context.ContextHandler
import com.mozhimen.emulatork.ui.startup.MainProcessInitializer
import com.mozhimen.emulatork.ui.startup.GameProcessInitializer
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class LemuroidApplication : dagger.android.support.DaggerApplication(), HasWorkerInjector {

    @Inject
    lateinit var workerInjector: DispatchingAndroidInjector<ListenableWorker>

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        val initializeComponent = if (isMainProcess()) {
            com.mozhimen.emulatork.ui.startup.MainProcessInitializer::class.java
        } else {
            com.mozhimen.emulatork.ui.startup.GameProcessInitializer::class.java
        }

        AppInitializer.getInstance(this).initializeComponent(initializeComponent)

        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        ContextHandler.attachBaseContext(base)
    }

    override fun applicationInjector(): AndroidInjector<out dagger.android.support.DaggerApplication> {
        return DaggerLemuroidApplicationComponent.builder().create(this)
    }

    override fun workerInjector(): AndroidInjector<ListenableWorker> = workerInjector
}
