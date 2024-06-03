package com.mozhimen.emulatork.core.source

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.netk.retrofit2.commons.DownloadApi

/**
 * @ClassName NoAssetsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class CoreSourceNo : CoreSource {
    override suspend fun clearCoreSource(storageProvider: StorageDirProvider) {}

    override suspend fun retrieveCoreSourceIfNeeded(downloadApi: DownloadApi, storageProvider: StorageDirProvider, sharedPreferences: SharedPreferences) {}
}
