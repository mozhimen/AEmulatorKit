package com.mozhimen.emulatork.core.source

import android.content.SharedPreferences
import android.net.Uri
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.netk.retrofit2.commons.DownloadApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.util.zip.ZipInputStream

/**
 * @ClassName PPSSPPAssetsManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
class CoreSourcePpsspp : CoreSource {

    companion object {
        const val PPSSPP_ASSETS_VERSION = "1.15"

        val PPSSPP_ASSETS_URL: Uri = Uri.parse("https://github.com/Swordfish90/LemuroidCores/")
            .buildUpon()
            .appendEncodedPath("raw/$PPSSPP_ASSETS_VERSION/assets/ppsspp.zip")
            .build()

        const val PPSSPP_ASSETS_VERSION_KEY = "ppsspp_assets_version_key"

        const val PPSSPP_ASSETS_FOLDER_NAME = "PPSSPP"
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    override suspend fun clearCoreSource(storageProvider: StorageDirProvider) {
        getAssetsDirectory(storageProvider).deleteRecursively()
    }

    override suspend fun retrieveCoreSourceIfNeeded(downloadApi: DownloadApi, storageProvider: StorageDirProvider, sharedPreferences: SharedPreferences) {
        if (!updatedRequested(storageProvider, sharedPreferences))
            return
        try {
            val response = downloadApi.downloadZip(PPSSPP_ASSETS_URL.toString())
            handleSuccess(storageProvider, response, sharedPreferences)
        } catch (e: Throwable) {
            getAssetsDirectory(storageProvider).deleteRecursively()
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    private suspend fun handleSuccess(storageProvider: StorageDirProvider, response: Response<ZipInputStream>, sharedPreferences: SharedPreferences) {
        val coreAssetsDirectory = getAssetsDirectory(storageProvider)
        coreAssetsDirectory.deleteRecursively()
        coreAssetsDirectory.mkdirs()

        response.body()?.use { zipInputStream ->
            while (true) {
                val entry = zipInputStream.nextEntry ?: break
                com.mozhimen.basick.utilk.android.util.UtilKLogWrapper.d(TAG,"Writing file: ${entry.name}")
                val destFile = File(
                    coreAssetsDirectory,
                    entry.name
                )
                if (entry.isDirectory) {
                    destFile.mkdirs()
                } else {
                    zipInputStream.copyTo(destFile.outputStream())
                }
            }
        }

        sharedPreferences.edit()
            .putString(PPSSPP_ASSETS_VERSION_KEY, PPSSPP_ASSETS_VERSION)
            .commit()
    }

    private suspend fun updatedRequested(storageProvider: StorageDirProvider, sharedPreferences: SharedPreferences): Boolean =
        withContext(Dispatchers.IO) {
            val directoryExists = getAssetsDirectory(storageProvider).exists()

            val currentVersion = sharedPreferences.getString(PPSSPP_ASSETS_VERSION_KEY, "none")
            val hasCurrentVersion = currentVersion == PPSSPP_ASSETS_VERSION

            !directoryExists || !hasCurrentVersion
        }

    private suspend fun getAssetsDirectory(storageProvider: StorageDirProvider): File {
        return withContext(Dispatchers.IO) {
            File(storageProvider.getInternalFileSystem(), PPSSPP_ASSETS_FOLDER_NAME)
        }
    }
}
