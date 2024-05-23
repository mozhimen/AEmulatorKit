package com.mozhimen.emulatork.basic.game.system

import com.mozhimen.emulatork.basic.R

/**
 * @ClassName MetaSystemID
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
fun GameSystem.metaSystemID() = GameSystemMetaID.fromSystemID(id)

/** Meta systems represents a collection of systems which appear the same to the user. It's currently
 *  only for Arcade (without separating FBNeo, MAME2000 or MAME2003). */
enum class GameSystemMetaID(val titleResId: Int, val imageResId: Int, val systemIDs: List<GameSystemID>) {
    NES(
        R.string.game_system_title_nes,
        R.drawable.game_system_nes,
        listOf(GameSystemID.NES)
    ),
    SNES(
        R.string.game_system_title_snes,
        R.drawable.game_system_snes,
        listOf(GameSystemID.SNES)
    ),
    GENESIS(
        R.string.game_system_title_genesis,
        R.drawable.game_system_genesis,
        listOf(GameSystemID.GENESIS, GameSystemID.SEGACD)
    ),
    GB(
        R.string.game_system_title_gb,
        R.drawable.game_system_gb,
        listOf(GameSystemID.GB)
    ),
    GBC(
        R.string.game_system_title_gbc,
        R.drawable.game_system_gbc,
        listOf(GameSystemID.GBC)
    ),
    GBA(
        R.string.game_system_title_gba,
        R.drawable.game_system_gba,
        listOf(GameSystemID.GBA)
    ),
    N64(
        R.string.game_system_title_n64,
        R.drawable.game_system_n64,
        listOf(GameSystemID.N64)
    ),
    SMS(
        R.string.game_system_title_sms,
        R.drawable.game_system_sms,
        listOf(GameSystemID.SMS)
    ),
    PSP(
        R.string.game_system_title_psp,
        R.drawable.game_system_psp,
        listOf(GameSystemID.PSP)
    ),
    NDS(
        R.string.game_system_title_nds,
        R.drawable.game_system_ds,
        listOf(GameSystemID.NDS)
    ),
    GG(
        R.string.game_system_title_gg,
        R.drawable.game_system_gg,
        listOf(GameSystemID.GG)
    ),
    ATARI2600(
        R.string.game_system_title_atari2600,
        R.drawable.game_system_atari2600,
        listOf(GameSystemID.ATARI2600)
    ),
    PSX(
        R.string.game_system_title_psx,
        R.drawable.game_system_psx,
        listOf(GameSystemID.PSX)
    ),
    ARCADE(
        R.string.game_system_title_arcade,
        R.drawable.game_system_arcade,
        listOf(GameSystemID.FBNEO, GameSystemID.MAME2003PLUS)
    ),
    ATARI7800(
        R.string.game_system_title_atari7800,
        R.drawable.game_system_atari7800,
        listOf(GameSystemID.ATARI7800)
    ),
    LYNX(
        R.string.game_system_title_lynx,
        R.drawable.game_system_lynx,
        listOf(GameSystemID.LYNX)
    ),
    PC_ENGINE(
        R.string.game_system_title_pce,
        R.drawable.game_system_pce,
        listOf(GameSystemID.PC_ENGINE)
    ),
    NGP(
        R.string.game_system_title_ngp,
        R.drawable.game_system_ngp,
        listOf(GameSystemID.NGP, GameSystemID.NGC)
    ),
    WS(
        R.string.game_system_title_ws,
        R.drawable.game_system_ws,
        listOf(GameSystemID.WS, GameSystemID.WSC)
    ),
    DOS(
        R.string.game_system_title_dos,
        R.drawable.game_system_dos,
        listOf(GameSystemID.DOS)
    ),
    NINTENDO_3DS(
        R.string.game_system_title_3ds,
        R.drawable.game_system_3ds,
        listOf(GameSystemID.NINTENDO_3DS)
    );

    companion object {
        fun fromSystemID(systemID: GameSystemID): GameSystemMetaID {
            return when (systemID) {
                GameSystemID.FBNEO -> ARCADE
                GameSystemID.MAME2003PLUS -> ARCADE
                GameSystemID.ATARI2600 -> ATARI2600
                GameSystemID.GB -> GB
                GameSystemID.GBC -> GBC
                GameSystemID.GBA -> GBA
                GameSystemID.GENESIS -> GENESIS
                GameSystemID.SEGACD -> GENESIS
                GameSystemID.GG -> GG
                GameSystemID.N64 -> N64
                GameSystemID.NDS -> NDS
                GameSystemID.NES -> NES
                GameSystemID.PSP -> PSP
                GameSystemID.PSX -> PSX
                GameSystemID.SMS -> SMS
                GameSystemID.SNES -> SNES
                GameSystemID.PC_ENGINE -> PC_ENGINE
                GameSystemID.LYNX -> LYNX
                GameSystemID.ATARI7800 -> ATARI7800
                GameSystemID.DOS -> DOS
                GameSystemID.NGP -> NGP
                GameSystemID.NGC -> NGP
                GameSystemID.WS -> WS
                GameSystemID.WSC -> WS
                GameSystemID.NINTENDO_3DS -> NINTENDO_3DS
            }
        }
    }
}
