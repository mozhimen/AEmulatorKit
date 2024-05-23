package com.mozhimen.emulatork.basic.deeplink

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mozhimen.emulatork.basic.game.db.entities.Game

/**
 * @ClassName DeepLink
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object DeepLink {

    fun openLeanbackUri(appContext: Context): Uri {
        return Uri.parse("lemuroid://${appContext.packageName}/open-leanback")
    }

    private fun uriForGame(appContext: Context, game: Game): Uri {
        return Uri.parse("lemuroid://${appContext.packageName}/play-game/id/${game.id}")
    }

    fun launchIntentForGame(appContext: Context, game: Game) =
        Intent(Intent.ACTION_VIEW, uriForGame(appContext, game))
}
