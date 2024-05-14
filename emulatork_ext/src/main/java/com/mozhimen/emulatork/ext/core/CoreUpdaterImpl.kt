package com.mozhimen.emulatork.ext.core

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.library.CoreID
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesHelper
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.util.files.safeDelete
import com.mozhimen.emulatork.util.kotlin.writeToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import timber.log.Timber
import java.io.File

/**
 * @ClassName CoreUpdaterImpl
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class CoreUpdaterImpl(
    private val directoriesManager: DirectoriesManager,
    retrofit: Retrofit
) : CoreUpdater {

    // This is the last tagged versions of cores.
    companion object {
        private const val CORES_VERSION = "1.15"
    }

    private val baseUri = Uri.parse("https://github.com/Swordfish90/LemuroidCores/")

    private val api = retrofit.create(CoreUpdater.CoreManagerApi::class.java)

    override suspend fun downloadCores(context: Context, coreIDs: List<CoreID>) {
        val sharedPreferences = SharedPreferencesHelper.getSharedPreferences(context.applicationContext)
        coreIDs.asFlow()
            .onEach { retrieveAssets(it, sharedPreferences) }
            .onEach { retrieveFile(context, it) }
            .collect()
    }

    private suspend fun retrieveFile(context: Context, coreID: CoreID) {
        findBundledLibrary(context, coreID) ?: downloadCoreFromGithub(coreID)
    }

    private suspend fun retrieveAssets(coreID: CoreID, sharedPreferences: SharedPreferences) {
        CoreID.getAssetManager(coreID)
            .retrieveAssetsIfNeeded(api, directoriesManager, sharedPreferences)
    }

    private suspend fun downloadCoreFromGithub(coreID: CoreID): File {
        Timber.i("Downloading core $coreID from github")

        val mainCoresDirectory = directoriesManager.getCoresDirectory()
        val coresDirectory = File(mainCoresDirectory, CORES_VERSION).apply {
            mkdirs()
        }

        val libFileName = coreID.libretroFileName
        val destFile = File(coresDirectory, libFileName)

        if (destFile.exists()) {
            return destFile
        }

        runCatching {
            deleteOutdatedCores(mainCoresDirectory, CORES_VERSION)
        }

        val uri = baseUri.buildUpon()
            .appendEncodedPath("raw/$CORES_VERSION/lemuroid_core_${coreID.coreName}/src/main/jniLibs/")
            .appendPath(Build.SUPPORTED_ABIS.first())
            .appendPath(libFileName)
            .build()

        try {
            downloadFile(uri, destFile)
            return destFile
        } catch (e: Throwable) {
            destFile.safeDelete()
            throw e
        }
    }

    private suspend fun downloadFile(uri: Uri, destFile: File) {
        val response = api.downloadFile(uri.toString())

        if (!response.isSuccessful) {
            Timber.e("Download core response was unsuccessful")
            throw Exception(response.errorBody()?.string() ?: "Download error")
        }

        response.body()?.writeToFile(destFile)
    }

    private suspend fun findBundledLibrary(
        context: Context,
        coreID: CoreID
    ): File? = withContext(Dispatchers.IO) {
        File(context.applicationInfo.nativeLibraryDir)
            .walkBottomUp()
            .firstOrNull { it.name == coreID.libretroFileName }
    }

    private fun deleteOutdatedCores(mainCoresDirectory: File, applicationVersion: String) {
        mainCoresDirectory.listFiles()
            ?.filter { it.name != applicationVersion }
            ?.forEach { it.deleteRecursively() }
    }
}
