package com.mozhimen.gamek.emulator.basic.library

import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.gojuno.koptional.rxjava2.filterSome
import com.mozhimen.gamek.emulator.basic.library.db.RetrogradeDatabase
import com.mozhimen.gamek.emulator.basic.library.db.mos.Game
import com.mozhimen.gamek.emulator.basic.storage.StorageFile
import com.mozhimen.gamek.emulator.basic.storage.StorageProvider
import com.mozhimen.gamek.emulator.basic.storage.StorageProviderRegistry
import com.mozhimen.utilk.gojuno.koptional.toSingleAsOptional
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

/**
 * @ClassName GameLibrary
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class GameLibrary(
    private val retrogradedb: RetrogradeDatabase,
    private val providerProviderRegistry: StorageProviderRegistry
) {

    fun indexGames(): Completable {
        val startedAtMs = System.currentTimeMillis()

        return Observable.fromIterable(providerProviderRegistry.enabledProviders).concatMap { provider ->
            provider.listFiles()
                .flatMapSingle { file -> retrieveGameFromFile(file) }
                .buffer(BUFFER_SIZE)
                .doOnNext { pairs -> updateExisting(pairs, startedAtMs) }
                .map { pairs -> pairs.map { (file, _) -> file } }
                .flatMapSingle { retrieveMetadata(it, provider, startedAtMs) }
                .doOnNext { games: List<Game> ->
                    games.forEach { Timber.d("Insert: $it") }
                    retrogradedb.gameDao().insert(games)
                }
                .doOnComplete { removeDeletedGames(startedAtMs) }
        }
            .subscribeOn(Schedulers.io())
            .ignoreElements()
    }

    private fun retrieveMetadata(it: List<StorageFile>, provider: StorageProvider, startedAtMs: Long): Single<List<Game>> {
        return Observable.fromIterable(it)
            .compose(provider.metadataProvider.transformer(startedAtMs))
            .filterSome()
            .toList()
    }

    private fun updateExisting(pairs: MutableList<Pair<StorageFile, Optional<Game>>>, startedAtMs: Long) {
        pairs.forEach { (file, game) -> Timber.d("Game already indexed? ${file.name} ${game is Some}") }
        pairs.filter { (_, game) -> game is Some }
            .map { (_, game) -> game.component1()!!.copy(lastIndexedAt = startedAtMs) }
            .let { games ->
                games.forEach { Timber.d("Update: $it") }
                retrogradedb.gameDao().update(games)
            }
    }

    private fun retrieveGameFromFile(file: StorageFile): Single<Pair<StorageFile, Optional<Game>>> {
        Timber.d("Retrieving game for file: $file ${file.uri}")
        return retrogradedb.gameDao().selectByFileUri(file.uri.toString())
            .toSingleAsOptional()
            .map { game -> Pair(file, game) }
    }

    private fun removeDeletedGames(startedAtMs: Long) {
        val games = retrogradedb.gameDao().selectByLastIndexedAtLessThan(startedAtMs)
        retrogradedb.gameDao().delete(games)
    }

    fun getGameRom(game: Game): Single<File> =
        providerProviderRegistry.getProvider(game).getGameRom(game)

    fun getGameSave(game: Game): Single<Optional<ByteArray>> =
        providerProviderRegistry.getProvider(game).getGameSave(game)

    fun setGameSave(game: Game, data: ByteArray): Completable =
        providerProviderRegistry.getProvider(game).setGameSave(game, data)

    companion object {
        // We batch database updates to avoid unnecessary UI updates.
        const val BUFFER_SIZE = 200
    }
}
