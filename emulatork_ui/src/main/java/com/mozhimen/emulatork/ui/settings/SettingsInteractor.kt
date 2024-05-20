package com.mozhimen.emulatork.ui.settings

import android.content.Context
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesHelper
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.ui.library.LibraryIndexScheduler
import com.mozhimen.emulatork.test.shared.storage.cache.CacheCleanerWork

/**
 * @ClassName SettingsInteractor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */

class SettingsInteractor(
    private val context: Context,
    private val directoriesManager: DirectoriesManager
) {
    fun changeLocalStorageFolder() {
        StorageFrameworkPickerLauncher.pickFolder(context)
    }

    fun resetAllSettings() {
        SharedPreferencesHelper.getLegacySharedPreferences(context).edit().clear().apply()
        SharedPreferencesHelper.getSharedPreferences(context).edit().clear().apply()
        com.mozhimen.emulatork.ui.library.LibraryIndexScheduler.scheduleLibrarySync(context.applicationContext)
        CacheCleanerWork.enqueueCleanCacheAll(context.applicationContext)
        deleteDownloadedCores()
    }

    private fun deleteDownloadedCores() {
        directoriesManager.getCoresDirectory()
            .listFiles()
            ?.forEach { runCatching { it.deleteRecursively() } }
    }
}
