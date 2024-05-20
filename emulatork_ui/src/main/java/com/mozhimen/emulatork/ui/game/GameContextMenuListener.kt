package com.mozhimen.emulatork.ui.game

import android.view.ContextMenu
import android.view.View
import com.mozhimen.emulatork.basic.library.db.entities.Game
import com.mozhimen.emulatork.test.R

/**
 * @ClassName GameContextMenuListener
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GameContextMenuListener(
    private val gameInteractor: GameInteractor,
    private val game: Game
) : View.OnCreateContextMenuListener {

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu.add(R.string.game_context_menu_resume).setOnMenuItemClickListener {
            gameInteractor.onGamePlay(game)
            true
        }

        menu.add(R.string.game_context_menu_restart).setOnMenuItemClickListener {
            gameInteractor.onGameRestart(game)
            true
        }

        if (game.isFavorite) {
            menu.add(R.string.game_context_menu_remove_from_favorites).setOnMenuItemClickListener {
                gameInteractor.onFavoriteToggle(game, false)
                true
            }
        } else {
            menu.add(R.string.game_context_menu_add_to_favorites).setOnMenuItemClickListener {
                gameInteractor.onFavoriteToggle(game, true)
                true
            }
        }

        if (gameInteractor.supportShortcuts()) {
            menu.add(R.string.game_context_menu_create_shortcut).setOnMenuItemClickListener {
                gameInteractor.onCreateShortcut(game)
                true
            }
        }
    }
}