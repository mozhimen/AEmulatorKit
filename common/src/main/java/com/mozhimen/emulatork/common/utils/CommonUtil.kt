package com.mozhimen.emulatork.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mozhimen.emulatork.basic.system.ESystemMetaType
import com.mozhimen.emulatork.common.system.SystemBundle
import com.mozhimen.emulatork.db.game.entities.Game

/**
 * @ClassName CommonUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/30
 * @Version 1.0
 */
fun SystemBundle.getSystemMetaType() = ESystemMetaType.getSystemMetaType(eSystemType)

object CommonUtil {
    fun launchIntentForGame(appContext: Context, scheme: String, game: Game) =
        Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://${appContext.packageName}/play-game/id/${game.id}"))
}
