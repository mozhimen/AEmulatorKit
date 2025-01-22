package com.mozhimen.emulatork.common.dagger

import androidx.work.ListenableWorker
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds

/**
 * @ClassName AndroidWorkerInjectionModule
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
@Module
abstract class AndroidWorkerInjectionModule {
    @Multibinds
    abstract fun workerInjectorFactories():
            Map<Class<out ListenableWorker>, AndroidInjector.Factory<out ListenableWorker>>

    @Multibinds
    abstract fun workerInjectorFactoriesWithStringKeys():
            Map<String, AndroidInjector.Factory<out ListenableWorker>>
}
