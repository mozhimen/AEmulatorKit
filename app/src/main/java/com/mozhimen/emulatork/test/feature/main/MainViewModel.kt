package com.mozhimen.emulatork.test.feature.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mozhimen.emulatork.test.shared.library.PendingOperationsMonitor

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

    val displayProgress = PendingOperationsMonitor(appContext).anyOperationInProgress()
}
