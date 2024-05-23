package com.mozhimen.emulatork.basic.assets

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.core.CoreManagerApi
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager

/**
 * @ClassName AssetsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/22
 * @Version 1.0
 */
interface AssetsManager {
    suspend fun retrieveAssetsIfNeeded(
        coreUpdaterApi: CoreManagerApi,
        storageDirectoriesManager: StorageDirectoriesManager,
        sharedPreferences: SharedPreferences
    )

    suspend fun clearAssets(storageDirectoriesManager: StorageDirectoriesManager)
}