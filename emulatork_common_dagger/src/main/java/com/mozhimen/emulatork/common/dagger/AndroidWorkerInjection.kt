package com.mozhimen.emulatork.common.dagger

import androidx.work.ListenableWorker
import com.mozhimen.emulatork.common.dagger.interfaces.HasWorkerInjector

/**
 * @ClassName AndroidWorkerInjection
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
object AndroidWorkerInjection {
    fun inject(worker: ListenableWorker) {
        checkNotNull(worker) { "worker" }
        val application = worker.applicationContext
        if (application !is HasWorkerInjector) {
            throw RuntimeException(
                "${application.javaClass.canonicalName} does not " +
                        "implement ${HasWorkerInjector::class.java.canonicalName}"
            )
        }
        val workerInjector = (application as HasWorkerInjector).workerInjector()
        checkNotNull(workerInjector) { "${application.javaClass}.workerInjector() return null" }
        workerInjector.inject(worker)
    }
}