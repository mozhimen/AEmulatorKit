package com.mozhimen.emulatork.test.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.mozhimen.emulatork.ui.library.PendingOperationsMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * @ClassName SettingsViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
class SettingsViewModel(
    context: Context,
    directoryPreference: String,
    sharedPreferences: FlowSharedPreferences
) : ViewModel() {

    class Factory(
        private val context: Context,
        private val sharedPreferences: FlowSharedPreferences
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val directoryPreference = context.getString(com.mozhimen.emulatork.basic.R.string.pref_key_extenral_folder)
            return SettingsViewModel(context, directoryPreference, sharedPreferences) as T
        }
    }

    val currentFolder = MutableStateFlow("")

    val indexingInProgress = com.mozhimen.emulatork.ui.library.PendingOperationsMonitor(context).anyLibraryOperationInProgress()

    val directoryScanInProgress = com.mozhimen.emulatork.ui.library.PendingOperationsMonitor(context).isDirectoryScanInProgress()

    init {
        viewModelScope.launch {
            sharedPreferences.getString(directoryPreference).asFlow()
                .flowOn(Dispatchers.IO)
                .collect { currentFolder.value = it }
        }
    }
}
