package com.mozhimen.emulatork.test

import com.mozhimen.emulatork.basic.game.GameSaveWorker
import com.mozhimen.emulatork.basic.injection.AndroidWorkerInjectionModule
import com.mozhimen.emulatork.basic.injection.PerApp
import com.mozhimen.emulatork.test.feature.library.LibraryIndexWork
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

/**
 * @ClassName LemuroidApplicationComponent
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AndroidWorkerInjectionModule::class,
    LemuroidApplicationModule::class,
    GameSaveWorker.Module::class,
    LibraryIndexWork.Module::class
    // GDriveApplicationModule::class,
    // WebDavApplicationModule::class,
])
@PerApp
interface LemuroidApplicationComponent : AndroidInjector<LemuroidApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<LemuroidApplication>()
}