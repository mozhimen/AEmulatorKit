package com.mozhimen.emulatork.test.shared

import android.content.Context
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.test.feature.game.GameLauncherActivity

/**
 * @ClassName GameInteractor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GameInteractor(
    private val context: Context,
    private val retrogradeDb: RetrogradeDatabase
) {
    fun onGamePlay(game: Game) {
        GameLauncherActivity.launchGame(context, game, true)
        retrogradeDb.gameDao().updateAsync(game.copy(lastPlayedAt = System.currentTimeMillis())).subscribe()
    }

    fun onGameRestart(game: Game) {
        GameLauncherActivity.launchGame(context, game, false)
        retrogradeDb.gameDao().updateAsync(game.copy(lastPlayedAt = System.currentTimeMillis())).subscribe()
    }

    fun onFavoriteToggle(game: Game, isFavorite: Boolean) {
        retrogradeDb.gameDao().updateAsync(game.copy(isFavorite = isFavorite)).subscribe()
    }
}