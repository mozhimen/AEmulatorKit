package com.mozhimen.gamek.emulator.test.dis

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @ClassName SingletonModule
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2023/9/27 9:46
 * @Version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
//    @Singleton
//    @Provides
//    fun provideApplicationContext(): Context = LoadApplication.instance.applicationContext
}