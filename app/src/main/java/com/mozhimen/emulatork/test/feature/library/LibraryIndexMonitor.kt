package com.mozhimen.emulatork.test.feature.library

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager

/**
 * @ClassName LibraryIndexMonitor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class LibraryIndexMonitor(private val appContext: Context) {

    fun getLiveData(): LiveData<Boolean> {
        val workInfosLiveData = WorkManager.getInstance(appContext).getWorkInfosForUniqueWorkLiveData(LibraryIndexWork.UNIQUE_WORK_ID)

        return workInfosLiveData.map { workInfos ->
            val isRunning = workInfos
                .map { it.state }
                .any { it in listOf(WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED) }
            isRunning
        }
    }
}