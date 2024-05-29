package com.mozhimen.emulatork.core.download

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.netk.retrofit2.commons.DownloadApi

/**
 * @ClassName NoAssetsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class CoreDownloadNo : CoreDownload {
    override suspend fun clearCores(storageProvider: StorageProvider) {}

    override suspend fun retrieveCoresIfNeeded(downloadApi: DownloadApi, storageProvider: StorageProvider, sharedPreferences: SharedPreferences) {}
}
