package com.mozhimen.emulatork.basic.storage.accessframework

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.leanback.preference.LeanbackPreferenceFragment
import androidx.preference.PreferenceManager
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.mozhimen.basick.utilk.android.database.cursor2sequence
import com.mozhimen.basick.utilk.java.io.getStrCrc32_use
import com.mozhimen.basick.utilk.kotlin.long2strCrc32
import com.mozhimen.emulatork.basic.R
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.basic.library.metadata.GameMetadataProvider
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.basic.storage.StorageProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
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
class StorageAccessFrameworkProvider(
    private val context: Context,
    override val metadataProvider: GameMetadataProvider,
    private val directoriesManager: DirectoriesManager
) : StorageProvider {

    override val id: String = "access_framework"

    override val name: String = context.getString(R.string.local_storage)

    override val uriSchemes = listOf("content")

    override val prefsFragmentClass: Class<LeanbackPreferenceFragment>? = null

    override val enabledByDefault = true

    override fun listFiles(): Observable<StorageFile> {
        return getExternalFolder()?.let { folder ->
            traverseDirectoryEntries(Uri.parse(folder)).map { handleFileUri(it) }
        } ?: Observable.empty()
    }

    private fun getExternalFolder(): String? {
        val prefString = context.getString(R.string.pref_key_extenral_folder)
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        return preferenceManager.getString(prefString, null)
    }

    private fun handleFileUri(fileUri: FileUri): StorageFile {
        return if (isZipped(fileUri.mime) && isSingleArchive(fileUri.uri)) {
            Timber.d("Detected single file archive. $name")
            handleUriAsSingleArchive(fileUri.uri)
        } else {
            Timber.d("Detected standard file. $name")
            handleUriAsStandardFile(fileUri.uri, fileUri.name, fileUri.size)
        }
    }

    private fun traverseDirectoryEntries(rootUri: Uri): Observable<FileUri> = Observable.create { emitter ->
        val contentResolver = context.contentResolver
        var currentNode = DocumentsContract
            .buildChildDocumentsUriUsingTree(rootUri, DocumentsContract
                .getTreeDocumentId(rootUri))

        try {
            // Keep track of our directory hierarchy
            val dirNodes = mutableListOf<Uri>()
            dirNodes.add(currentNode)

            while (dirNodes.isNotEmpty()) {
                currentNode = dirNodes.removeAt(0)

                val projection = arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    DocumentsContract.Document.COLUMN_SIZE
                )

                Timber.d("Detected node uri: $currentNode")

                contentResolver.query(currentNode, projection, null, null, null)?.use { cursor ->
                    cursor.cursor2sequence().forEach {
                        val docId = it.getString(0)
                        val name = it.getString(1)
                        val mime = it.getString(2)
                        val size = it.getLong(3)

                        if (isDirectory(mime)) {
                            val newNode = DocumentsContract.buildChildDocumentsUriUsingTree(currentNode, docId)
                            dirNodes.add(newNode)
                            Timber.d("Detected subfolder: $id, name: $name")
                        } else {
                            val uri = DocumentsContract.buildDocumentUriUsingTree(rootUri, docId)
                            emitter.onNext(FileUri(uri, name, size, mime))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emitter.onError(e)
        }

        emitter.onComplete()
    }

    private fun handleUriAsSingleArchive(uri: Uri): StorageFile {
        ZipInputStream(context.contentResolver.openInputStream(uri)).use {
            val entry = it.nextEntry

            Timber.d("Processing zipped entry: ${entry.name}")

            return StorageFile(entry.name, entry.size, entry.crc.long2strCrc32(), uri)
        }
    }

    private fun handleUriAsStandardFile(uri: Uri, name: String, size: Long): StorageFile {
        val crc32 = context.contentResolver.openInputStream(uri)?.getStrCrc32_use()

        Timber.d("Detected file: $id, name: $name, crc: $crc32")

        return StorageFile(name, size, crc32, uri)
    }

    private fun isDirectory(mimeType: String) = DocumentsContract.Document.MIME_TYPE_DIR == mimeType

    private fun isZipped(mimeType: String) = mimeType == ZIP_MIME_TYPE

    private fun isSingleArchive(uri: Uri): Boolean {
        ZipInputStream(context.contentResolver.openInputStream(uri)).use {
            return it.nextEntry != null && it.nextEntry == null
        }
    }

    override fun getGameRom(game: Game): Single<File> = Single.fromCallable {
        val gamesCacheDir = File(context.cacheDir, SAF_CACHE_SUBFOLDER)
        gamesCacheDir.mkdirs()
        val gameFile = File(gamesCacheDir, game.fileName)
        if (gameFile.exists()) {
            return@fromCallable gameFile
        }

        val mimeType = context.contentResolver.getType(game.fileUri)

        if (mimeType == ZIP_MIME_TYPE) {
            val stream = ZipInputStream(context.contentResolver.openInputStream(game.fileUri))
            copyZipInputStreamToFile(gameFile, stream)
        } else {
            val stream = context.contentResolver.openInputStream(game.fileUri)!!
            copyInputStreamToFile(gameFile, stream)
        }
        gameFile
    }

    private fun copyInputStreamToFile(gameFile: File, inputFileStream: InputStream) {
        inputFileStream.use { inputStream ->
            gameFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    private fun copyZipInputStreamToFile(gameFile: File, zipInputFileStream: ZipInputStream) {
        zipInputFileStream.use { zipInputStream ->
            zipInputStream.nextEntry
            copyInputStreamToFile(gameFile, zipInputStream)
        }
    }

    override fun getGameSave(game: Game): Single<Optional<ByteArray>> {
        val saveFile = getSaveFile(game)
        return if (saveFile.exists()) {
            Single.just(saveFile.readBytes().toOptional())
        } else {
            Single.just(None)
        }
    }

    override fun setGameSave(game: Game, data: ByteArray): Completable = Completable.fromCallable {
        val saveFile = getSaveFile(game)
        saveFile.writeBytes(data)
    }

    private fun getSaveFile(game: Game): File {
        val statesDirectories = directoriesManager.getStatesDirectory()
        return File(statesDirectories, "${game.fileName}.state")
    }

    private data class FileUri(val uri: Uri, val name: String, val size: Long, val mime: String)

    companion object {
        const val SAF_CACHE_SUBFOLDER = "storage-framework-games"
        const val ZIP_MIME_TYPE = "application/zip"
    }
}
