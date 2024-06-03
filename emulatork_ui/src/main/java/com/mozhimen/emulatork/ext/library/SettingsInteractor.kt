package com.mozhimen.emulatork.ext.library

import android.content.Context
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.ext.works.WorkScheduler
import com.mozhimen.emulatork.ui.settings.AbsStorageFrameworkPickerActivity
import com.mozhimen.emulatork.ui.works.AbsWorkLibraryIndex
import com.mozhimen.emulatork.ui.works.AbsWorkStorageCacheCleaner

/**
 * @ClassName SettingsInteractor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */

class SettingsInteractor constructor(
    private val context: Context,
    private val storageProvider: StorageDirProvider
) : IUtilK {
    fun changeLocalStorageFolder(storageFrameworkPickerActivityClazz: Class<out AbsStorageFrameworkPickerActivity>) {
        AbsStorageFrameworkPickerActivity.pickFolder(context, storageFrameworkPickerActivityClazz)
    }

    fun resetAllSettings(workLibraryIndexClazz: Class<out AbsWorkLibraryIndex>, workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>) {
        SharedPreferencesManager.getLegacySharedPreferences(context).edit().clear().apply()
        SharedPreferencesManager.getSharedPreferences(context).edit().clear().apply()
        WorkScheduler.scheduleLibrarySync(workLibraryIndexClazz, context)
        WorkScheduler.enqueueCleanCacheAll(workStorageCacheCleanerClazz, context)
        deleteDownloadedCores()
    }

    private fun deleteDownloadedCores() {
        storageProvider.getInternalFileCores()
            .listFiles()
            ?.forEach { runCatching { it.deleteRecursively() } }
    }
}
