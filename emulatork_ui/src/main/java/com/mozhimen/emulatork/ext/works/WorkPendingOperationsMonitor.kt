package com.mozhimen.emulatork.ext.works

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mozhimen.basick.utilk.androidx.lifecycle.liveData2combinedLiveData
import com.mozhimen.basick.utilk.androidx.lifecycle.liveData2throttledLiveData
import com.mozhimen.basick.utilk.androidx.lifecycle.map

/**
 * @ClassName PendingOperationsMonitor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class WorkPendingOperationsMonitor(private val appContext: Context) {

    enum class Operation(val uniqueId: String, val isPeriodic: Boolean) {
        LIBRARY_INDEX(WorkScheduler.WORK_ID_LIBRARY_INDEX, false),
        CORE_UPDATE(WorkScheduler.WORK_ID_CORE_UPDATE, false),
        SAVES_SYNC_PERIODIC(WorkScheduler.WORK_ID_SAVE_SYNC_PERIODIC, true),
        SAVES_SYNC_ONE_SHOT(WorkScheduler.WORK_ID_SAVE_SYNC, false)
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun anyOperationInProgress(): LiveData<Boolean> {
        return operationsInProgress(*Operation.values())
    }

    fun anySaveOperationInProgress(): LiveData<Boolean> {
        return operationsInProgress(Operation.SAVES_SYNC_ONE_SHOT, Operation.SAVES_SYNC_PERIODIC)
    }

    fun anyLibraryOperationInProgress(): LiveData<Boolean> {
        return operationsInProgress(Operation.LIBRARY_INDEX, Operation.CORE_UPDATE)
    }

    fun isDirectoryScanInProgress(): LiveData<Boolean> {
        return operationsInProgress(Operation.LIBRARY_INDEX)
    }

    //////////////////////////////////////////////////////////////////////////////////

    private fun operationsInProgress(vararg operations: Operation): LiveData<Boolean> {
        return operations
            .map { operationInProgress(it) }
            .reduce { first, second -> first.liveData2combinedLiveData(second) { b1, b2 -> b1 || b2 } }
            .liveData2throttledLiveData(100)
    }

    private fun operationInProgress(operation: Operation): LiveData<Boolean> {
        return WorkManager.getInstance(appContext)
            .getWorkInfosForUniqueWorkLiveData(operation.uniqueId)
            .map { if (operation.isPeriodic) isPeriodicJobRunning(it) else isJobRunning(it) }
    }

    private fun isJobRunning(workInfos: List<WorkInfo>): Boolean {
        return workInfos
            .map { it.state }
            .any { it in listOf(WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED) }
    }

    private fun isPeriodicJobRunning(workInfos: List<WorkInfo>): Boolean {
        return workInfos
            .map { it.state }
            .any { it in listOf(WorkInfo.State.RUNNING) }
    }
}
