package com.mozhimen.emulatork.core.download

import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.netk.retrofit2.commons.DownloadApi

/**
 * @ClassName AssetsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/22
 * @Version 1.0
 */
interface CoreDownload {
    suspend fun retrieveCoresIfNeeded(downloadApi: DownloadApi, storageProvider: StorageProvider, sharedPreferences: SharedPreferences)
    suspend fun clearCores(storageProvider: StorageProvider)
}