package com.mozhimen.emulatork.basic.storage.local

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.leanback.preference.LeanbackPreferenceFragment
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.mozhimen.basick.utilk.java.io.gerStrCrc_use
import com.mozhimen.emulatork.basic.R
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.basic.library.metadata.GameMetadataProvider
import com.mozhimen.emulatork.basic.storage.StorageFile
import com.mozhimen.emulatork.basic.storage.StorageProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

/**
 * @ClassName LocalStorageProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
// I'm keeping this class here only because it might be needed in the future, but we should rely on SAF now.

@Deprecated("This class is no longer needed use the LocalStorageAccessFrameworkProvider class.")
class LocalStorageProvider(
    private val context: Context,
    override val metadataProvider: GameMetadataProvider,
    private val searchOnlyPrivateDirectories: Boolean = false
) : StorageProvider {

    override val id: String = "local"

    override val name: String = context.getString(R.string.local_storage)

    override val uriSchemes = listOf("file")

    override val prefsFragmentClass: Class<LeanbackPreferenceFragment>? = null

    override val enabledByDefault = true

    override fun listFiles(): Observable<StorageFile> = Observable.empty()
    /*Single.fromCallable {
searchableDirectories()
        .map { walkDirectory(it) }
        .reduce { acc, iterable -> acc union iterable }
}*/

    private fun searchableDirectories(): List<File> = if (searchOnlyPrivateDirectories) {
        listOf(*context.getExternalFilesDirs(null))
    } else {
        listOf(Environment.getExternalStorageDirectory())
    }

    private fun walkDirectory(directory: File): Iterable<StorageFile> {
        return directory.walk()
            .filter { file -> file.isFile && file.name.startsWith(".").not() }
            .map { file ->
                StorageFile(
                    name = file.name,
                    size = file.length(),
                    crc = file.gerStrCrc_use().toUpperCase(),
                    uri = Uri.parse(file.toURI().toString()))
            }
            .asIterable()
    }

    override fun getGameRom(game: Game): Single<File> = Single.fromCallable {
        File(game.fileUri.path)
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
        val retrogradeDir = File(Environment.getExternalStorageDirectory(), "retrograde")
        val savesDir = File(retrogradeDir, "saves")
        savesDir.mkdirs()
        return File(savesDir, "${game.fileName}.sram")
    }
}
