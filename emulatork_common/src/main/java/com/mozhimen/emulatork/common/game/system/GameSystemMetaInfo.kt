package com.mozhimen.emulatork.common.game.system

import android.content.Context

/**
 * @ClassName MetaSystemInfo
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
data class GameSystemMetaInfo(val metaSystem: GameSystemMetaID, val count: Int) {
    fun getName(context: Context) = context.resources.getString(metaSystem.titleResId)
}
