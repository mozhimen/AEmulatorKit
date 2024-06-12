package com.mozhimen.emulatork.ext.game

import android.content.Context
import com.mozhimen.basick.utilk.android.widget.showToast
import com.mozhimen.emulatork.common.android.BusyProvider
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.game.AbsGameActivity
import com.mozhimen.emulatork.ext.covers.CoverShortcutGenerator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @ClassName GameInteractor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
open class GameInteractor constructor(
    private val activity: Context,
    private val gameActivityClazz: Class<out AbsGameActivity>,
    private val retrogradeDb: RetrogradeDatabase,
    private val useLeanback: Boolean,
    private val coverShortcutGenerator: CoverShortcutGenerator,
    private val gameLauncher: GameLauncher
) {
    fun onGamePlay(game: Game) {
        if (activity !is BusyProvider) return
        if (activity.isBusy()) {
            R.string.game_interactory_busy.showToast()
            return
        }
        gameLauncher.launchGameAsync(activity.activity(),gameActivityClazz, game, true, useLeanback)
    }

    fun onGameRestart(game: Game) {
        if (activity !is BusyProvider) return
        if (activity.isBusy()) {
            R.string.game_interactory_busy.showToast()
            return
        }
        gameLauncher.launchGameAsync(activity.activity(),gameActivityClazz, game, false, useLeanback)
    }

    fun onFavoriteToggle(game: Game, isFavorite: Boolean) {
        GlobalScope.launch {
            retrogradeDb.gameDao().update(game.copy(isFavorite = isFavorite))
        }
    }

    fun onCreateShortcut(game: Game, scheme: String) {
        GlobalScope.launch {
            coverShortcutGenerator.pinShortcutForGame(game,scheme)
        }
    }

    fun supportShortcuts(): Boolean {
        return coverShortcutGenerator.supportShortcuts()
    }
}