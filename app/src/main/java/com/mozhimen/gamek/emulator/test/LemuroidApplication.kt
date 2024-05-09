package com.mozhimen.gamek.emulator.test

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import com.mozhimen.basick.BuildConfig
import com.mozhimen.gamek.emulator.basic.injection.HasWorkerInjector
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class LemuroidApplication : DaggerApplication(), HasWorkerInjector {
    companion object {
        fun get(context: Context) = context.applicationContext as LemuroidApplication
    }

    /*@Inject
    lateinit var rxTimberTree: RxTimberTree
    @Inject
    lateinit var rxPrefs: RxSharedPreferences
    @Inject
    lateinit var gdriveStorageProvider: GDriveStorageProvider*/

    @Inject
    lateinit var workerInjector: DispatchingAndroidInjector<ListenableWorker>

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        initializeWorkManager()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } /*else {
            Bugsnag.init(this)
        }*/

        // var isPlanted = false
        /* rxPrefs.getBoolean(getString(R.string.pref_key_flags_logging)).asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { value ->
                    gdriveStorageProvider.loggingEnabled = value
                    if (value) {
                        Timber.plant(rxTimberTree)
                        isPlanted = true
                    } else {
                        if (isPlanted) {
                            Timber.uproot(rxTimberTree)
                            isPlanted = false
                        }
                    }
                }*/
    }

    private fun initializeWorkManager() {
        val config = Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()

        WorkManager.initialize(this, config)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerLemuroidApplicationComponent.builder().create(this)
    }

    override fun workerInjector(): AndroidInjector<ListenableWorker> = workerInjector
}
