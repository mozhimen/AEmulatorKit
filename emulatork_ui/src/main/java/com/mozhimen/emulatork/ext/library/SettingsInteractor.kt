package com.mozhimen.emulatork.ext.library

import android.content.Context
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesMgr
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager
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
    private val storageDirectoriesManager: StorageDirectoriesManager
) : IUtilK {
    fun changeLocalStorageFolder(storageFrameworkPickerActivityClazz: Class<out AbsStorageFrameworkPickerActivity>) {
        AbsStorageFrameworkPickerActivity.pickFolder(context, storageFrameworkPickerActivityClazz)
    }

    fun resetAllSettings(workLibraryIndexClazz: Class<out AbsWorkLibraryIndex>, workStorageCacheCleanerClazz: Class<out AbsWorkStorageCacheCleaner>) {
        SharedPreferencesMgr.getLegacySharedPreferences(context).edit().clear().apply()
        SharedPreferencesMgr.getSharedPreferences(context).edit().clear().apply()
        WorkScheduler.scheduleLibrarySync(TAG,workLibraryIndexClazz, context.applicationContext)
        WorkScheduler.enqueueCleanCacheAll(TAG,workStorageCacheCleanerClazz, context.applicationContext)
        deleteDownloadedCores()
    }

    private fun deleteDownloadedCores() {
        storageDirectoriesManager.getCoresDirectory()
            .listFiles()
            ?.forEach { runCatching { it.deleteRecursively() } }
    }
}
