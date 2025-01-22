package com.mozhimen.emulatork.test.dagger

import android.annotation.SuppressLint
import androidx.startup.AppInitializer
import androidx.work.ListenableWorker
import com.google.android.material.color.DynamicColors
import com.mozhimen.basick.utilk.android.content.isMainProcess
import com.mozhimen.emulatork.common.dagger.interfaces.HasWorkerInjector
import com.mozhimen.emulatork.ui.dagger.startup.MainProcessInitializer
import com.mozhimen.emulatork.basic.startup.GameProcessInitializer
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
            MainProcessInitializer::class.java
        } else {
            GameProcessInitializer::class.java
        }

        AppInitializer.getInstance(this).initializeComponent(initializeComponent)

        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun applicationInjector(): AndroidInjector<out dagger.android.support.DaggerApplication> {
        return DaggerLemuroidApplicationComponent.builder().create(this)
    }

    override fun workerInjector(): AndroidInjector<ListenableWorker> = workerInjector
}
