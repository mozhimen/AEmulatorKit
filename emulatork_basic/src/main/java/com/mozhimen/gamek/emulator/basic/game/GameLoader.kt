package com.mozhimen.gamek.emulator.basic.game

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.mozhimen.gamek.emulator.basic.core.CoreManager
import com.mozhimen.gamek.emulator.basic.library.GameLibrary
import com.mozhimen.gamek.emulator.basic.library.GameSystem
import com.mozhimen.gamek.emulator.basic.library.db.RetrogradeDatabase
import com.mozhimen.gamek.emulator.basic.library.db.mos.Game
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * @ClassName GameLoader
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class GameLoader(
    private val coreManager: CoreManager,
    private val retrogradeDatabase: RetrogradeDatabase,
    private val gameLibrary: GameLibrary
) {

    fun loadGame(gameId: Int): Maybe<Game> = retrogradeDatabase.gameDao().selectById(gameId)

    fun load(gameId: Int, loadSave: Boolean): Single<GameData> {
        return loadGame(gameId)
            .subscribeOn(Schedulers.io())
            .flatMapSingle { game -> prepareGame(game, loadSave) }
    }

    private fun prepareGame(game: Game, loadSave: Boolean): Single<GameData> {
        val gameSystem = GameSystem.findById(game.systemId)!!

        val coreObservable = coreManager.downloadCore(gameSystem.coreFileName)
        val gameObservable = gameLibrary.getGameRom(game)
        val saveObservable = if (loadSave) {
            gameLibrary.getGameSave(game)
        } else {
            Single.just(None)
        }

        return Single.zip(
            coreObservable,
            gameObservable,
            saveObservable,
            Function3<File, File, Optional<ByteArray>, GameData> { coreFile, gameFile, saveData ->
                GameData(game, coreFile, gameFile, saveData.toNullable())
            })
    }

    @Suppress("ArrayInDataClass")
    data class GameData(
        val game: Game,
        val coreFile: File,
        val gameFile: File,
        val saveData: ByteArray?
    )
}