package com.mozhimen.emulatork.basic.storage

import androidx.leanback.preference.LeanbackPreferenceFragment
import com.gojuno.koptional.Optional
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.basic.library.metadata.GameMetadataProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

/**
 * @ClassName StorageProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
interface StorageProvider {

    val id: String

    val name: String

    val uriSchemes: List<String>

    val prefsFragmentClass: Class<out LeanbackPreferenceFragment>?

    val metadataProvider: GameMetadataProvider

    val enabledByDefault: Boolean

    fun listFiles(): Observable<StorageFile>

    fun getGameRom(game: Game): Single<File>

    fun getGameSave(game: Game): Single<Optional<ByteArray>>

    fun setGameSave(game: Game, data: ByteArray): Completable
}