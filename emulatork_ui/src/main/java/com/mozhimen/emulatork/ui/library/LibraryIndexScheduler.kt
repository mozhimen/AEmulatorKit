package com.mozhimen.emulatork.ui.library

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * @ClassName LibraryIndexScheduler
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object LibraryIndexScheduler {
    val CORE_UPDATE_WORK_ID: String = AbsCoreUpdateWork::class.java.simpleName
    val LIBRARY_INDEX_WORK_ID: String = AbsLibraryIndexWork::class.java.simpleName

    fun scheduleLibrarySync(applicationContext: Context) {
        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                LIBRARY_INDEX_WORK_ID,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequestBuilder<AbsLibraryIndexWork>().build()
            )
            .enqueue()
    }

    fun scheduleCoreUpdate(applicationContext: Context) {
        WorkManager.getInstance(applicationContext)
            .beginUniqueWork(
                CORE_UPDATE_WORK_ID,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequestBuilder<AbsCoreUpdateWork>().build()
            )
            .enqueue()
    }

    fun cancelLibrarySync(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(LIBRARY_INDEX_WORK_ID)
    }

    fun cancelCoreUpdate(applicationContext: Context) {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(CORE_UPDATE_WORK_ID)
    }
}
