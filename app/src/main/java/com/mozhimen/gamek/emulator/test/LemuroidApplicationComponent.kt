package com.mozhimen.gamek.emulator.test

import com.mozhimen.gamek.emulator.basic.game.GameSaveWorker
import com.mozhimen.gamek.emulator.basic.injection.AndroidWorkerInjectionModule
import com.mozhimen.gamek.emulator.basic.injection.PerApp
import com.mozhimen.gamek.emulator.test.feature.library.LibraryIndexWork
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