package com.mozhimen.emulatork.test.hilt.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mozhimen.emulatork.ext.works.WorkPendingOperationsMonitor

/**
 * @ClassName MainViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class MainViewModel(appContext: Context) : ViewModel() {

    class Factory(private val appContext: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(appContext) as T
        }
    }

    val displayProgress = WorkPendingOperationsMonitor(appContext).anyOperationInProgress()
}
