package com.mozhimen.emulatork.basic.injection

import androidx.work.ListenableWorker
import dagger.android.AndroidInjector

/**
 * @ClassName HasWorkerInjector
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
interface HasWorkerInjector {
    fun workerInjector(): AndroidInjector<ListenableWorker>
}