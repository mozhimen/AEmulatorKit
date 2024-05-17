package com.mozhimen.emulatork.test.shared

import com.mozhimen.basick.utilk.android.widget.showToast
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.feature.shortcuts.ShortcutsGenerator
import com.mozhimen.emulatork.test.shared.game.GameLauncher
import com.mozhimen.emulatork.test.shared.main.BusyActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @ClassName GameInteractor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GameInteractor(
    private val activity: BusyActivity,
    private val retrogradeDb: RetrogradeDatabase,
    private val useLeanback: Boolean,
    private val shortcutsGenerator: ShortcutsGenerator,
    private val gameLauncher: GameLauncher
) {
    fun onGamePlay(game: Game) {
        if (activity.isBusy()) {
            R.string.game_interactory_busy.showToast()
            return
        }
        gameLauncher.launchGameAsync(activity.activity(), game, true, useLeanback)
    }

    fun onGameRestart(game: Game) {
        if (activity.isBusy()) {
            R.string.game_interactory_busy.showToast()
            return
        }
        gameLauncher.launchGameAsync(activity.activity(), game, false, useLeanback)
    }

    fun onFavoriteToggle(game: Game, isFavorite: Boolean) {
        GlobalScope.launch {
            retrogradeDb.gameDao().update(game.copy(isFavorite = isFavorite))
        }
    }

    fun onCreateShortcut(game: Game) {
        GlobalScope.launch {
            shortcutsGenerator.pinShortcutForGame(game)
        }
    }

    fun supportShortcuts(): Boolean {
        return shortcutsGenerator.supportShortcuts()
    }
}