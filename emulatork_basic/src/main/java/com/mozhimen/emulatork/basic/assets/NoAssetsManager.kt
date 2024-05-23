package com.mozhimen.emulatork.basic.assets

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.core.CoreManagerApi
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager

/**
 * @ClassName NoAssetsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class NoAssetsManager : AssetsManager {

    override suspend fun clearAssets(storageDirectoriesManager: StorageDirectoriesManager) {}

    override suspend fun retrieveAssetsIfNeeded(
        coreUpdaterApi: CoreManagerApi,
        storageDirectoriesManager: StorageDirectoriesManager,
        sharedPreferences: SharedPreferences
    ) {}
}
