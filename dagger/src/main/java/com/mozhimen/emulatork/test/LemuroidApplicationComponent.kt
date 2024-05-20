package com.mozhimen.emulatork.test

import com.mozhimen.emulatork.basic.dagger.AndroidWorkerInjectionModule
import com.mozhimen.emulatork.basic.dagger.interfaces.PerApp
import com.mozhimen.emulatork.ui.library.LibraryIndexWork
import com.mozhimen.emulatork.ui.library.CoreUpdateWork
import com.mozhimen.emulatork.ui.savesync.SaveSyncWork
import com.mozhimen.emulatork.ui.storage.cache.CacheCleanerWork
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
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AndroidWorkerInjectionModule::class,
        LemuroidApplicationModule::class,
        com.mozhimen.emulatork.ui.library.LibraryIndexWork.Module::class,
        com.mozhimen.emulatork.ui.savesync.SaveSyncWork.Module::class,
//        ChannelUpdateWork.Module::class,
        com.mozhimen.emulatork.ui.library.CoreUpdateWork.Module::class,
        com.mozhimen.emulatork.ui.storage.cache.CacheCleanerWork.Module::class,
//        LemuroidTVApplicationModule::class
    ]
)
@PerApp
interface LemuroidApplicationComponent : AndroidInjector<LemuroidApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<LemuroidApplication>()
}