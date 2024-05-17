package com.mozhimen.emulatork.test

import com.mozhimen.emulatork.basic.dagger.AndroidWorkerInjectionModule
import com.mozhimen.emulatork.basic.dagger.PerApp
import com.mozhimen.emulatork.ui.dagger.shared.library.LibraryIndexWork
import com.mozhimen.emulatork.ui.dagger.shared.library.CoreUpdateWork
import com.mozhimen.emulatork.test.shared.savesync.SaveSyncWork
import com.mozhimen.emulatork.test.shared.storage.cache.CacheCleanerWork
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
        LibraryIndexWork.Module::class,
        SaveSyncWork.Module::class,
//        ChannelUpdateWork.Module::class,
        CoreUpdateWork.Module::class,
        CacheCleanerWork.Module::class,
//        LemuroidTVApplicationModule::class
    ]
)
@PerApp
interface LemuroidApplicationComponent : AndroidInjector<LemuroidApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<LemuroidApplication>()
}