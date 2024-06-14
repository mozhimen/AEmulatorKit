package com.mozhimen.emulatork.common.storage

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.mozhimen.basick.utilk.java.io.isFileZipped
import com.mozhimen.basick.utilk.java.util.extractEntryToFile_use
import com.mozhimen.emulatork.common.R
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.basic.rom.SRomFileType
import com.mozhimen.emulatork.basic.storage.DocumentFileParser
import com.mozhimen.emulatork.basic.storage.StorageBaseFile
import com.mozhimen.emulatork.db.game.entities.GameDataFile
import com.mozhimen.emulatork.db.game.entities.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * @ClassName LocalStorageProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class StorageProviderLocal(
    private val context: Context,
    private val storageDirProvider: StorageDirProvider
) : StorageProvider {

    override val id: String = "local"

    override val name: String = context.getString(R.string.local_storage)

    override val uriSchemes = listOf("file")

//    override val prefsFragmentClass: Class<LeanbackPreferenceFragment>? = null

    override val enabledByDefault = true

    override fun listStorageBaseFiles(): Flow<List<StorageBaseFile>> =
        walkDirectory(getExternalFolder() ?: storageDirProvider.getExternalFileRoms())

    override fun getStorageFile(storageBaseFile: StorageBaseFile): StorageFile? {
        return DocumentFileParser.parseDocumentFile(context, storageBaseFile)
    }

    private fun getExternalFolder(): File? {
        val prefString = context.getString(R.string.pref_key_legacy_external_folder)
        val preferenceManager = SharedPreferencesManager.getLegacySharedPreferences(context)
        return preferenceManager.getString(prefString, null)?.let { File(it) }
    }

    private fun walkDirectory(rootDirectory: File): Flow<List<StorageBaseFile>> = flow {
        val directories = mutableListOf(rootDirectory)

        while (directories.isNotEmpty()) {
            val directory = directories.removeAt(0)
            val groups = directory.listFiles()
                ?.filterNot { it.name.startsWith(".") }
                ?.groupBy { it.isDirectory } ?: mapOf()

            val newDirectories = groups[true] ?: listOf()
            val newFiles = groups[false] ?: listOf()

            directories.addAll(newDirectories)
            emit((newFiles.map { StorageBaseFile(it.name, it.length(), it.toUri(), it.path) }))
        }
    }

    // There is no need to handle anything. Data file have to be in the same directory for detection we expect them
    // to still be there.
    private fun getDataFile(gameDataFile: GameDataFile): File {
        val dataFilePath = Uri.parse(gameDataFile.fileUri).path
        return File(dataFilePath)
    }

    private fun getGameRom(game: Game): File {
        val gamePath = Uri.parse(game.fileUri).path
        val originalFile = File(gamePath)
        if (!originalFile.isFileZipped() || originalFile.name == game.fileName) {
            return originalFile
        }

        val cacheFile = StorageUtil.getCacheFileForGame(StorageDirProvider.LOCAL_STORAGE_CACHE_SUBFOLDER, context, game)
        if (cacheFile.exists()) {
            return cacheFile
        }

        if (originalFile.isFileZipped()) {
            val stream = ZipInputStream(originalFile.inputStream())
            stream.extractEntryToFile_use(game.fileName, cacheFile)
        }

        return cacheFile
    }

    override fun getGameRomFiles(
        game: Game,
        gameDataFiles: List<GameDataFile>,
        allowVirtualFiles: Boolean
    ): SRomFileType {
        return SRomFileType.Standard(listOf(getGameRom(game)) + gameDataFiles.map { getDataFile(it) })
    }

    override fun getInputStream(uri: Uri): InputStream {
        return File(uri.path).inputStream()
    }
}

