package com.mozhimen.emulatork.basic.bios

import com.mozhimen.emulatork.basic.game.system.GameSystemID

/**
 * @ClassName Bios
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
data class Bios(
    val libretroFileName: String,
    val md5: String,
    val description: String,
    val systemID: GameSystemID,
    val externalCRC32: String? = null,
    val externalName: String? = null,
) {
    fun displayName() = externalName ?: libretroFileName
}
