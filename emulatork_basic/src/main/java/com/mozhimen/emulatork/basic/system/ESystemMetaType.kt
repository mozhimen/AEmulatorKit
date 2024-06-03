package com.mozhimen.emulatork.basic.system

import com.mozhimen.emulatork.basic.R

/**
 * @ClassName ESystemMetaType
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/30
 * @Version 1.0
 */
/** Meta systems represents a collection of systems which appear the same to the user. It's currently
 *  only for Arcade (without separating FBNeo, MAME2000 or MAME2003). */
enum class ESystemMetaType(val titleResId: Int, val imageResId: Int, val systemIDs: List<ESystemType>) {
    NES(
        R.string.game_system_title_nes,
        R.drawable.game_system_nes,
        listOf(ESystemType.NES)
    ),
    SNES(
        R.string.game_system_title_snes,
        R.drawable.game_system_snes,
        listOf(ESystemType.SNES)
    ),
    GENESIS(
        R.string.game_system_title_genesis,
        R.drawable.game_system_genesis,
        listOf(ESystemType.GENESIS, ESystemType.SEGACD)
    ),
    GB(
        R.string.game_system_title_gb,
        R.drawable.game_system_gb,
        listOf(ESystemType.GB)
    ),
    GBC(
        R.string.game_system_title_gbc,
        R.drawable.game_system_gbc,
        listOf(ESystemType.GBC)
    ),
    GBA(
        R.string.game_system_title_gba,
        R.drawable.game_system_gba,
        listOf(ESystemType.GBA)
    ),
    N64(
        R.string.game_system_title_n64,
        R.drawable.game_system_n64,
        listOf(ESystemType.N64)
    ),
    SMS(
        R.string.game_system_title_sms,
        R.drawable.game_system_sms,
        listOf(ESystemType.SMS)
    ),
    PSP(
        R.string.game_system_title_psp,
        R.drawable.game_system_psp,
        listOf(ESystemType.PSP)
    ),
    NDS(
        R.string.game_system_title_nds,
        R.drawable.game_system_ds,
        listOf(ESystemType.NDS)
    ),
    GG(
        R.string.game_system_title_gg,
        R.drawable.game_system_gg,
        listOf(ESystemType.GG)
    ),
    ATARI2600(
        R.string.game_system_title_atari2600,
        R.drawable.game_system_atari2600,
        listOf(ESystemType.ATARI2600)
    ),
    PSX(
        R.string.game_system_title_psx,
        R.drawable.game_system_psx,
        listOf(ESystemType.PSX)
    ),
    ARCADE(
        R.string.game_system_title_arcade,
        R.drawable.game_system_arcade,
        listOf(ESystemType.FBNEO, ESystemType.MAME2003PLUS)
    ),
    ATARI7800(
        R.string.game_system_title_atari7800,
        R.drawable.game_system_atari7800,
        listOf(ESystemType.ATARI7800)
    ),
    LYNX(
        R.string.game_system_title_lynx,
        R.drawable.game_system_lynx,
        listOf(ESystemType.LYNX)
    ),
    PC_ENGINE(
        R.string.game_system_title_pce,
        R.drawable.game_system_pce,
        listOf(ESystemType.PC_ENGINE)
    ),
    NGP(
        R.string.game_system_title_ngp,
        R.drawable.game_system_ngp,
        listOf(ESystemType.NGP, ESystemType.NGC)
    ),
    WS(
        R.string.game_system_title_ws,
        R.drawable.game_system_ws,
        listOf(ESystemType.WS, ESystemType.WSC)
    ),
    DOS(
        R.string.game_system_title_dos,
        R.drawable.game_system_dos,
        listOf(ESystemType.DOS)
    ),
    NINTENDO_3DS(
        R.string.game_system_title_3ds,
        R.drawable.game_system_3ds,
        listOf(ESystemType.NINTENDO_3DS)
    );

    companion object {
        @JvmStatic
        fun getSystemMetaType(eSystemType: ESystemType): ESystemMetaType {
            return when (eSystemType) {
                ESystemType.FBNEO -> ARCADE
                ESystemType.MAME2003PLUS -> ARCADE
                ESystemType.ATARI2600 -> ATARI2600
                ESystemType.GB -> GB
                ESystemType.GBC -> GBC
                ESystemType.GBA -> GBA
                ESystemType.GENESIS -> GENESIS
                ESystemType.SEGACD -> GENESIS
                ESystemType.GG -> GG
                ESystemType.N64 -> N64
                ESystemType.NDS -> NDS
                ESystemType.NES -> NES
                ESystemType.PSP -> PSP
                ESystemType.PSX -> PSX
                ESystemType.SMS -> SMS
                ESystemType.SNES -> SNES
                ESystemType.PC_ENGINE -> PC_ENGINE
                ESystemType.LYNX -> LYNX
                ESystemType.ATARI7800 -> ATARI7800
                ESystemType.DOS -> DOS
                ESystemType.NGP -> NGP
                ESystemType.NGC -> NGP
                ESystemType.WS -> WS
                ESystemType.WSC -> WS
                ESystemType.NINTENDO_3DS -> NINTENDO_3DS
            }
        }
    }
}