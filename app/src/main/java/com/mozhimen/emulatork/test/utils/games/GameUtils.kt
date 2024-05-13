package com.mozhimen.emulatork.test.utils.games

import android.content.Context
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.db.mos.Game

/**
 * @ClassName GameUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameUtils {

    companion object {
        fun getGameSubtitle(context: Context, game: Game): String {
            val systemName = getSystemNameForGame(context, game)
            val developerName = if (game.developer?.isNotBlank() == true) {
                "- ${game.developer}"
            } else {
                ""
            }
            return "$systemName $developerName"
        }

        private fun getSystemNameForGame(context: Context, game: Game): String {
            val systemTitleResource = GameSystem.findById(game.systemId).shortTitleResId
            return context.getString(systemTitleResource)
        }
    }
}
