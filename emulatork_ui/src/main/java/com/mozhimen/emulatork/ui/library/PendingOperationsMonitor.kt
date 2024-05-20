package com.mozhimen.emulatork.ui.library

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mozhimen.emulatork.test.shared.savesync.SaveSyncWork
import com.mozhimen.emulatork.test.utils.livedata.combineLatest
import com.mozhimen.emulatork.test.utils.livedata.map
import com.mozhimen.emulatork.test.utils.livedata.throttle

/**
 * @ClassName PendingOperationsMonitor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class PendingOperationsMonitor(private val appContext: Context) {

    enum class Operation(val uniqueId: String, val isPeriodic: Boolean) {
        LIBRARY_INDEX(com.mozhimen.emulatork.ui.library.LibraryIndexScheduler.LIBRARY_INDEX_WORK_ID, false),
        CORE_UPDATE(com.mozhimen.emulatork.ui.library.LibraryIndexScheduler.CORE_UPDATE_WORK_ID, false),
        SAVES_SYNC_PERIODIC(SaveSyncWork.UNIQUE_PERIODIC_WORK_ID, true),
        SAVES_SYNC_ONE_SHOT(SaveSyncWork.UNIQUE_WORK_ID, false)
    }

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

    private fun operationsInProgress(vararg operations: Operation): LiveData<Boolean> {
        return operations
            .map { operationInProgress(it) }
            .reduce { first, second -> first.combineLatest(second) { b1, b2 -> b1 || b2 } }
            .throttle(100)
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
