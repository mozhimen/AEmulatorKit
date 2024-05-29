package com.mozhimen.emulatork.common.game

import android.content.Context
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.game.system.GameSystems

/**
 * @ClassName GameUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameUtil {

    companion object {
        fun getGameSubtitle(context: Context, game: Game): String {
            val systemName = getSystemNameForGame(context, game)
            val developerName = if (game.developer?.isNotBlank() == true) {
                "- ${game.developer}"
            } else ""
            return "$systemName $developerName"
        }

        private fun getSystemNameForGame(context: Context, game: Game): String {
            val systemTitleResource = GameSystems.findById(game.systemId).shortTitleResId
            return context.getString(systemTitleResource)
        }
    }
}
