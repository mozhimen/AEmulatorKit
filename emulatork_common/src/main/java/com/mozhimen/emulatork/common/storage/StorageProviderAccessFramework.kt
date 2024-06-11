package com.mozhimen.emulatork.common.storage

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.mozhimen.basick.elemk.androidx.documentfile.isTypeZip
import com.mozhimen.basick.utilk.java.io.inputStream2file_use_ofCopyTo
import com.mozhimen.basick.utilk.java.util.extractEntryToFile_use
import com.mozhimen.emulatork.common.R
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.basic.storage.StorageBaseFile
import com.mozhimen.emulatork.basic.rom.SRomFileType
import com.mozhimen.emulatork.basic.storage.DocumentFileParser
import com.mozhimen.emulatork.db.game.entities.GameDataFile
import com.mozhimen.emulatork.db.game.entities.Game
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
class StorageProviderAccessFramework(private val context: Context) : StorageProvider {

    companion object {
        const val VIRTUAL_FILE_PATH = "/virtual/file/path"
    }

    ///////////////////////////////////////////////////////////////////////////////

    override val id: String = "access_framework"

    override val name: String = context.getString(R.string.local_storage)

    override val uriSchemes = listOf("content")

//    override val prefsFragmentClass: Class<LeanbackPreferenceFragment>? = null

    override val enabledByDefault = true

    override fun listStorageBaseFiles(): Flow<List<StorageBaseFile>> {
        return getExternalFolder()?.let { folder ->
            traverseDirectoryEntries(Uri.parse(folder))
        } ?: emptyFlow()
    }

    override fun getStorageFile(storageBaseFile: StorageBaseFile): StorageFile? {
        return DocumentFileParser.parseDocumentFile(context, storageBaseFile)
    }

    override fun getGameRomFiles(game: Game, gameDataFiles: List<GameDataFile>, allowVirtualFiles: Boolean): SRomFileType {
        val originalDocumentUri = Uri.parse(game.fileUri)
        val originalDocument = DocumentFile.fromSingleUri(context, originalDocumentUri)!!

        val isZipped = originalDocument.isTypeZip() && originalDocument.name != game.fileName

        return when {
            isZipped && gameDataFiles.isEmpty() -> getGameRomFilesZipped(game, originalDocument)
            allowVirtualFiles -> getGameRomFilesVirtual(game, gameDataFiles)
            else -> getGameRomFilesStandard(game, gameDataFiles, originalDocument)
        }
    }

    override fun getInputStream(uri: Uri): InputStream? {
        return context.contentResolver.openInputStream(uri)
    }

    ///////////////////////////////////////////////////////////////////////////////

    private fun getExternalFolder(): String? {
        val prefString = context.getString(R.string.pref_key_extenral_folder)
        val preferenceManager = SharedPreferencesManager.getLegacySharedPreferences(context)
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

    private fun getGameRomFilesStandard(game: Game, gameDataFiles: List<GameDataFile>, originalDocument: DocumentFile): SRomFileType {
        val gameEntry = getGameRomStandard(game, originalDocument)
        val dataEntries = gameDataFiles.map { getDataFileStandard(game, it) }
        return SRomFileType.Standard(listOf(gameEntry) + dataEntries)
    }

    private fun getGameRomFilesZipped(game: Game, originalDocument: DocumentFile): SRomFileType {
        val cacheFile = StorageUtil.getCacheFileForGame(StorageDirProvider.SAF_CACHE_SUBFOLDER, context, game)
        if (cacheFile.exists()) {
            return SRomFileType.Standard(listOf(cacheFile))
        }

        val stream = ZipInputStream(
            context.contentResolver.openInputStream(originalDocument.uri)
        )
        stream.extractEntryToFile_use(game.fileName, cacheFile)
        return SRomFileType.Standard(listOf(cacheFile))
    }

    private fun getGameRomFilesVirtual(game: Game, gameDataFiles: List<GameDataFile>): SRomFileType {
        val gameEntry = getGameRomVirtual(game)
        val dataEntries = gameDataFiles.map { getDataFileVirtual(it) }
        return SRomFileType.Virtual(listOf(gameEntry) + dataEntries)
    }

    private fun getDataFileVirtual(gameDataFile: GameDataFile): SRomFileType.Virtual.Entry {
        return SRomFileType.Virtual.Entry(
            "$VIRTUAL_FILE_PATH/${gameDataFile.fileName}",
            context.contentResolver.openFileDescriptor(Uri.parse(gameDataFile.fileUri), "r")!!
        )
    }

    private fun getDataFileStandard(game: Game, gameDataFile: GameDataFile): File {
        val cacheFile = StorageUtil.getDataFileForGame(
            StorageDirProvider.SAF_CACHE_SUBFOLDER,
            context,
            game,
            gameDataFile
        )

        if (cacheFile.exists()) {
            return cacheFile
        }

        val stream = context.contentResolver.openInputStream(Uri.parse(gameDataFile.fileUri))!!
        stream.inputStream2file_use_ofCopyTo(cacheFile)
        return cacheFile
    }

    private fun getGameRomVirtual(game: Game): SRomFileType.Virtual.Entry {
        return SRomFileType.Virtual.Entry(
            "$VIRTUAL_FILE_PATH/${game.fileName}",
            context.contentResolver.openFileDescriptor(Uri.parse(game.fileUri), "r")!!
        )
    }

    private fun getGameRomStandard(game: Game, originalDocument: DocumentFile): File {
        val cacheFile = StorageUtil.getCacheFileForGame(StorageDirProvider.SAF_CACHE_SUBFOLDER, context, game)

        if (cacheFile.exists()) {
            return cacheFile
        }

        val stream = context.contentResolver.openInputStream(originalDocument.uri)!!
        stream.inputStream2file_use_ofCopyTo(cacheFile)
        return cacheFile
    }
}
