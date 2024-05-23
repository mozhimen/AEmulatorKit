package com.mozhimen.emulatork.basic.assets

import android.content.SharedPreferences
import android.net.Uri
import com.mozhimen.emulatork.basic.core.CoreManagerApi
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager
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
class PPSSPPAssetsManager : AssetsManager {

    override suspend fun clearAssets(storageDirectoriesManager: StorageDirectoriesManager) {
        getAssetsDirectory(storageDirectoriesManager).deleteRecursively()
    }

    override suspend fun retrieveAssetsIfNeeded(
        coreUpdaterApi: CoreManagerApi,
        storageDirectoriesManager: StorageDirectoriesManager,
        sharedPreferences: SharedPreferences
    ) {
        if (!updatedRequested(storageDirectoriesManager, sharedPreferences)) {
            return
        }

        try {
            val response = coreUpdaterApi.downloadZip(PPSSPP_ASSETS_URL.toString())
            handleSuccess(storageDirectoriesManager, response, sharedPreferences)
        } catch (e: Throwable) {
            getAssetsDirectory(storageDirectoriesManager).deleteRecursively()
        }
    }

    private suspend fun handleSuccess(
        storageDirectoriesManager: StorageDirectoriesManager,
        response: Response<ZipInputStream>,
        sharedPreferences: SharedPreferences
    ) {
        val coreAssetsDirectory = getAssetsDirectory(storageDirectoriesManager)
        coreAssetsDirectory.deleteRecursively()
        coreAssetsDirectory.mkdirs()

        response.body()?.use { zipInputStream ->
            while (true) {
                val entry = zipInputStream.nextEntry ?: break
                Timber.d("Writing file: ${entry.name}")
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

    private suspend fun updatedRequested(
        storageDirectoriesManager: StorageDirectoriesManager,
        sharedPreferences: SharedPreferences
    ): Boolean = withContext(Dispatchers.IO) {
        val directoryExists = getAssetsDirectory(storageDirectoriesManager).exists()

        val currentVersion = sharedPreferences.getString(PPSSPP_ASSETS_VERSION_KEY, "none")
        val hasCurrentVersion = currentVersion == PPSSPP_ASSETS_VERSION

        !directoryExists || !hasCurrentVersion
    }

    private suspend fun getAssetsDirectory(storageDirectoriesManager: StorageDirectoriesManager): File {
        return withContext(Dispatchers.IO) {
            File(storageDirectoriesManager.getSystemDirectory(), PPSSPP_ASSETS_FOLDER_NAME)
        }
    }

    companion object {
        const val PPSSPP_ASSETS_VERSION = "1.15"

        val PPSSPP_ASSETS_URL: Uri = Uri.parse("https://github.com/Swordfish90/LemuroidCores/")
            .buildUpon()
            .appendEncodedPath("raw/$PPSSPP_ASSETS_VERSION/assets/ppsspp.zip")
            .build()

        const val PPSSPP_ASSETS_VERSION_KEY = "ppsspp_assets_version_key"

        const val PPSSPP_ASSETS_FOLDER_NAME = "PPSSPP"
    }
}
