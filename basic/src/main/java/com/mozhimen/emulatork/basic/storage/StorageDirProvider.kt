package com.mozhimen.emulatork.basic.storage

import android.content.Context
import java.io.File

/**
 * @ClassName DirectoriesManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class StorageDirProvider(private val appContext: Context) {
    companion object {
        const val SAF_CACHE_SUBFOLDER = "storage-framework-games"
        const val LOCAL_STORAGE_CACHE_SUBFOLDER = "local-storage-games"
    }

    @Deprecated("Use the external states directory")
    fun getInternalFileStates(): File =
        File(appContext.filesDir, "states").apply { mkdirs() }

    fun getInternalFileCores(): File =
        File(appContext.filesDir, "cores").apply { mkdirs() }

    fun getInternalFileSystem(): File =
        File(appContext.filesDir, "system").apply { mkdirs() }

    fun getExternalFileStates(): File =
        File(appContext.getExternalFilesDir(null), "states").apply { mkdirs() }

    fun getExternalFileStatePreviews(): File =
        File(appContext.getExternalFilesDir(null), "state-previews").apply { mkdirs() }

    fun getExternalFileSaves(): File =
        File(appContext.getExternalFilesDir(null), "saves").apply { mkdirs() }

    fun getExternalFileRoms(): File =
        File(appContext.getExternalFilesDir(null), "roms").apply { mkdirs() }
}
