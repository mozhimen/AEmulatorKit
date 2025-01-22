package com.mozhimen.emulatork.test.dagger

import com.mozhimen.emulatork.common.dagger.AndroidWorkerInjectionModule
import com.mozhimen.emulatork.common.dagger.annors.PerApp
import com.mozhimen.emulatork.ui.dagger.works.WorkLibraryIndex
import com.mozhimen.emulatork.ui.dagger.works.WorkCoreUpdate
import com.mozhimen.emulatork.ui.dagger.works.WorkSaveSync
import com.mozhimen.emulatork.ui.dagger.works.WorkStorageCacheCleaner
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
        WorkLibraryIndex.Module::class,
        WorkSaveSync.Module::class,
//        ChannelUpdateWork.Module::class,
        WorkCoreUpdate.Module::class,
        WorkStorageCacheCleaner.Module::class,
//        LemuroidTVApplicationModule::class
    ]
)
@PerApp
interface LemuroidApplicationComponent : AndroidInjector<LemuroidApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<LemuroidApplication>()
}