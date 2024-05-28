package com.mozhimen.emulatork.test.hilt.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.mozhimen.emulatork.ext.works.WorkPendingOperationsMonitor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
class SettingsViewModel constructor(
    context: Context,
     private val directoryPreference: String,
    private val sharedPreferences: FlowSharedPreferences
) : ViewModel() {

    companion object {
        fun provideFactory(context: Context,flowSharedPreferences: FlowSharedPreferences): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val directoryPreference = context.getString(com.mozhimen.emulatork.basic.R.string.pref_key_extenral_folder)
                    return SettingsViewModel(context, directoryPreference,flowSharedPreferences) as T
                }
            }
    }

    val currentFolder = MutableStateFlow("")

    val indexingInProgress = WorkPendingOperationsMonitor(context).anyLibraryOperationInProgress()

    val directoryScanInProgress = WorkPendingOperationsMonitor(context).isDirectoryScanInProgress()

    fun refreshData() {
        viewModelScope.launch {
            sharedPreferences.getString(directoryPreference).asFlow()
                .flowOn(Dispatchers.IO)
                .collect { currentFolder.value = it }
        }
    }
}
