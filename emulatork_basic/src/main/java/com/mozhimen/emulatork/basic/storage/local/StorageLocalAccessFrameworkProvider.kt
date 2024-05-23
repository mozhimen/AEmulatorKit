package com.mozhimen.emulatork.basic.storage.local

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.mozhimen.abilityk.jetpack.documentfile.utils.isZipped
import com.mozhimen.basick.utilk.java.io.inputStream2file_use_ofCopyTo
import com.mozhimen.basick.utilk.java.util.extractEntryToFile_use
import com.mozhimen.emulatork.basic.R
import com.mozhimen.emulatork.basic.game.db.entities.DataFile
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesMgr
import com.mozhimen.emulatork.basic.storage.StorageBaseFile
import com.mozhimen.emulatork.basic.storage.StorageRomFile
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.basic.storage.StorageProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * @ClassName StorageAccessFrameworkProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class StorageLocalAccessFrameworkProvider(private val context: Context) : StorageProvider {

    companion object {
        const val SAF_CACHE_SUBFOLDER = "storage-framework-games"
        const val VIRTUAL_FILE_PATH = "/virtual/file/path"
    }

    ///////////////////////////////////////////////////////////////////////////////

    override val id: String = "access_framework"

    override val name: String = context.getString(R.string.local_storage)

    override val uriSchemes = listOf("content")

//    override val prefsFragmentClass: Class<LeanbackPreferenceFragment>? = null

    override val enabledByDefault = true

    override fun listBaseStorageFiles(): Flow<List<StorageBaseFile>> {
        return getExternalFolder()?.let { folder ->
            traverseDirectoryEntries(Uri.parse(folder))
        } ?: emptyFlow()
    }

    override fun getStorageFile(storageBaseFile: StorageBaseFile): StorageFile? {
        return DocumentFileParser.parseDocumentFile(context, storageBaseFile)
    }

    override fun getGameRomFiles(game: Game, dataFiles: List<DataFile>, allowVirtualFiles: Boolean): StorageRomFile {
        val originalDocumentUri = Uri.parse(game.fileUri)
        val originalDocument = DocumentFile.fromSingleUri(context, originalDocumentUri)!!

        val isZipped = originalDocument.isZipped() && originalDocument.name != game.fileName

        return when {
            isZipped && dataFiles.isEmpty() -> getGameRomFilesZipped(game, originalDocument)
            allowVirtualFiles -> getGameRomFilesVirtual(game, dataFiles)
            else -> getGameRomFilesStandard(game, dataFiles, originalDocument)
        }
    }

    override fun getInputStream(uri: Uri): InputStream? {
        return context.contentResolver.openInputStream(uri)
    }

    ///////////////////////////////////////////////////////////////////////////////

    private fun getExternalFolder(): String? {
        val prefString = context.getString(R.string.pref_key_extenral_folder)
        val preferenceManager = SharedPreferencesMgr.getLegacySharedPreferences(context)
        return preferenceManager.getString(prefString, null)
    }

    private fun traverseDirectoryEntries(rootUri: Uri): Flow<List<StorageBaseFile>> = flow {
        val directoryDocumentIds = mutableListOf<String>()
        DocumentsContract.getTreeDocumentId(rootUri)?.let { directoryDocumentIds.add(it) }

        while (directoryDocumentIds.isNotEmpty()) {
            val currentDirectoryDocumentId = directoryDocumentIds.removeAt(0)

            val result = runCatching {
                listBaseStorageFiles(rootUri, currentDirectoryDocumentId)
            }
            if (result.isFailure) {
                Timber.e(result.exceptionOrNull(), "Error while listing files")
            }

            val (files, directories) = result.getOrDefault(
                listOf<StorageBaseFile>() to listOf<String>()
            )

            emit(files)
            directoryDocumentIds.addAll(directories)
        }
    }

    private fun listBaseStorageFiles(treeUri: Uri, rootDocumentId: String): Pair<List<StorageBaseFile>, List<String>> {
        val resultFiles = mutableListOf<StorageBaseFile>()
        val resultDirectories = mutableListOf<String>()

        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, rootDocumentId)

        Timber.d("Querying files in directory: $childrenUri")

        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_MIME_TYPE
        )
        context.contentResolver.query(childrenUri, projection, null, null, null)?.use {
            while (it.moveToNext()) {
                val documentId = it.getString(0)
                val documentName = it.getString(1)
                val documentSize = it.getLong(2)
                val mimeType = it.getString(3)

                if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                    resultDirectories.add(documentId)
                } else {
                    val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                        treeUri,
                        documentId
                    )
                    resultFiles.add(
                        StorageBaseFile(
                            name = documentName,
                            size = documentSize,
                            uri = documentUri,
                            path = documentUri.path
                        )
                    )
                }
            }
        }

        return resultFiles to resultDirectories
    }

    private fun getGameRomFilesStandard(game: Game, dataFiles: List<DataFile>, originalDocument: DocumentFile): StorageRomFile {
        val gameEntry = getGameRomStandard(game, originalDocument)
        val dataEntries = dataFiles.map { getDataFileStandard(game, it) }
        return StorageRomFile.Standard(listOf(gameEntry) + dataEntries)
    }

    private fun getGameRomFilesZipped(game: Game, originalDocument: DocumentFile): StorageRomFile {
        val cacheFile = StorageLocalUtil.getCacheFileForGame(SAF_CACHE_SUBFOLDER, context, game)
        if (cacheFile.exists()) {
            return StorageRomFile.Standard(listOf(cacheFile))
        }

        val stream = ZipInputStream(
            context.contentResolver.openInputStream(originalDocument.uri)
        )
        stream.extractEntryToFile_use(game.fileName, cacheFile)
        return StorageRomFile.Standard(listOf(cacheFile))
    }

    private fun getGameRomFilesVirtual(game: Game, dataFiles: List<DataFile>): StorageRomFile {
        val gameEntry = getGameRomVirtual(game)
        val dataEntries = dataFiles.map { getDataFileVirtual(it) }
        return StorageRomFile.Virtual(listOf(gameEntry) + dataEntries)
    }

    private fun getDataFileVirtual(dataFile: DataFile): StorageRomFile.Virtual.Entry {
        return StorageRomFile.Virtual.Entry(
            "$VIRTUAL_FILE_PATH/${dataFile.fileName}",
            context.contentResolver.openFileDescriptor(Uri.parse(dataFile.fileUri), "r")!!
        )
    }

    private fun getDataFileStandard(game: Game, dataFile: DataFile): File {
        val cacheFile = StorageLocalUtil.getDataFileForGame(
            SAF_CACHE_SUBFOLDER,
            context,
            game,
            dataFile
        )

        if (cacheFile.exists()) {
            return cacheFile
        }

        val stream = context.contentResolver.openInputStream(Uri.parse(dataFile.fileUri))!!
        stream.inputStream2file_use_ofCopyTo(cacheFile)
        return cacheFile
    }

    private fun getGameRomVirtual(game: Game): StorageRomFile.Virtual.Entry {
        return StorageRomFile.Virtual.Entry(
            "$VIRTUAL_FILE_PATH/${game.fileName}",
            context.contentResolver.openFileDescriptor(Uri.parse(game.fileUri), "r")!!
        )
    }

    private fun getGameRomStandard(game: Game, originalDocument: DocumentFile): File {
        val cacheFile = StorageLocalUtil.getCacheFileForGame(SAF_CACHE_SUBFOLDER, context, game)

        if (cacheFile.exists()) {
            return cacheFile
        }

        val stream = context.contentResolver.openInputStream(originalDocument.uri)!!
        stream.inputStream2file_use_ofCopyTo(cacheFile)
        return cacheFile
    }
}
