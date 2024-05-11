package com.mozhimen.emulatork.basic.core.assetsmanager

import android.content.SharedPreferences
import android.net.Uri
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.library.CoreID
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
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
class PPSSPPAssetsManager : CoreID.AssetsManager {

    override suspend fun clearAssets(directoriesManager: DirectoriesManager) {
        getAssetsDirectory(directoriesManager).deleteRecursively()
    }

    override suspend fun retrieveAssetsIfNeeded(
        coreUpdaterApi: CoreUpdater.CoreManagerApi,
        directoriesManager: DirectoriesManager,
        sharedPreferences: SharedPreferences
    ) {
        if (!updatedRequested(directoriesManager, sharedPreferences)) {
            return
        }

        try {
            val response = coreUpdaterApi.downloadZip(PPSSPP_ASSETS_URL.toString())
            handleSuccess(directoriesManager, response, sharedPreferences)
        } catch (e: Throwable) {
            getAssetsDirectory(directoriesManager).deleteRecursively()
        }
    }

    private suspend fun handleSuccess(
        directoriesManager: DirectoriesManager,
        response: Response<ZipInputStream>,
        sharedPreferences: SharedPreferences
    ) {
        val coreAssetsDirectory = getAssetsDirectory(directoriesManager)
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
        directoriesManager: DirectoriesManager,
        sharedPreferences: SharedPreferences
    ): Boolean = withContext(Dispatchers.IO) {
        val directoryExists = getAssetsDirectory(directoriesManager).exists()

        val currentVersion = sharedPreferences.getString(PPSSPP_ASSETS_VERSION_KEY, "none")
        val hasCurrentVersion = currentVersion == PPSSPP_ASSETS_VERSION

        !directoryExists || !hasCurrentVersion
    }

    private suspend fun getAssetsDirectory(directoriesManager: DirectoriesManager): File {
        return withContext(Dispatchers.IO) {
            File(directoriesManager.getSystemDirectory(), PPSSPP_ASSETS_FOLDER_NAME)
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
