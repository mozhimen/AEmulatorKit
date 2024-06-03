package com.mozhimen.emulatork.core.download

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import com.mozhimen.basick.utilk.java.io.deleteFile
import com.mozhimen.basick.utilk.java.io.inputStream2file_use_ofCopyTo
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.core.ECoreType
import com.mozhimen.emulatork.core.utils.getCoreSource
import com.mozhimen.netk.retrofit2.commons.DownloadApi
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
class CoreDownloaderImpl(
    private val storageProvider: StorageDirProvider, retrofit: Retrofit
) : CoreDownload {

    // This is the last tagged versions of cores.
    companion object {
        private const val CORES_VERSION = "1.15"
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private val baseUri = Uri.parse("https://github.com/Swordfish90/LemuroidCores/")

    private val api = retrofit.create(DownloadApi::class.java)

    /////////////////////////////////////////////////////////////////////////////////////

    override suspend fun downloadCores(context: Context, eCoreTypes: List<ECoreType>) {
        val sharedPreferences = SharedPreferencesManager.getSharedPreferences(context.applicationContext)
        eCoreTypes.asFlow()
            .onEach { retrieveAssets(it, sharedPreferences) }
            .onEach { retrieveFile(context, it) }
            .collect()
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private suspend fun retrieveFile(context: Context, coreID: ECoreType) {
        findBundledLibrary(context, coreID) ?: downloadCoreFromGithub(coreID)
    }

    private suspend fun retrieveAssets(eCoreType: ECoreType, sharedPreferences: SharedPreferences) {
        eCoreType.getCoreSource()
            .retrieveCoreSourceIfNeeded(api, storageProvider, sharedPreferences)
    }

    private suspend fun downloadCoreFromGithub(coreID: ECoreType): File {
        Timber.i("Downloading core $coreID from github")

        val mainCoresDirectory = storageProvider.getInternalFileCores()
        val coresDirectory = File(mainCoresDirectory, CORES_VERSION).apply {
            mkdirs()
        }

        val libFileName = coreID.coreSoFileName
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
            destFile.deleteFile()
            throw e
        }
    }

    private suspend fun downloadFile(uri: Uri, destFile: File) {
        val response = api.downloadFile(uri.toString())

        if (!response.isSuccessful) {
            Timber.e("Download core response was unsuccessful")
            throw Exception(response.errorBody()?.string() ?: "Download error")
        }

        response.body()?.inputStream2file_use_ofCopyTo(destFile)
    }

    private suspend fun findBundledLibrary(
        context: Context,
        coreID: ECoreType
    ): File? = withContext(Dispatchers.IO) {
        File(context.applicationInfo.nativeLibraryDir)
            .walkBottomUp()
            .firstOrNull { it.name == coreID.coreSoFileName }
    }

    private fun deleteOutdatedCores(mainCoresDirectory: File, applicationVersion: String) {
        mainCoresDirectory.listFiles()
            ?.filter { it.name != applicationVersion }
            ?.forEach { it.deleteRecursively() }
    }
}
