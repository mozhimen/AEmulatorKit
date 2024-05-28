package com.mozhimen.emulatork.basic.controller.touch

import com.mozhimen.emulatork.input.kind.EInputKind

/**
 * @ClassName TouchControllerID
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
enum class ControllerTouchID {
    GB,
    NES,
    DESMUME,
    MELONDS,
    PSX,
    PSX_DUALSHOCK,
    N64,
    PSP,
    SNES,
    GBA,
    GENESIS_3,
    GENESIS_6,
    ATARI2600,
    SMS,
    GG,
    ARCADE_4,
    ARCADE_6,
    LYNX,
    ATARI7800,
    PCE,
    NGP,
    DOS,
    WS_LANDSCAPE,
    WS_PORTRAIT,
    NINTENDO_3DS;

    class Config(
        val leftConfig: EInputKind,
        val rightConfig: EInputKind,
        val leftScale: Float = 1.0f,
        val rightScale: Float = 1.0f,
        val verticalMarginDP: Float = 0f
    )

    companion object {
        fun getConfig(id: ControllerTouchID): Config {
            return when (id) {
                GB -> Config(
                    EInputKind.GB_LEFT,
                    EInputKind.GB_RIGHT
                )
                NES -> Config(
                    EInputKind.NES_LEFT,
                    EInputKind.NES_RIGHT
                )
                DESMUME -> Config(
                    EInputKind.DESMUME_LEFT,
                    EInputKind.DESMUME_RIGHT
                )
                MELONDS -> Config(
                    EInputKind.MELONDS_NDS_LEFT,
                    EInputKind.MELONDS_NDS_RIGHT
                )
                PSX -> Config(
                    EInputKind.PSX_LEFT,
                    EInputKind.PSX_RIGHT
                )
                PSX_DUALSHOCK -> Config(
                    EInputKind.PSX_DUALSHOCK_LEFT,
                    EInputKind.PSX_DUALSHOCK_RIGHT
                )
                N64 -> Config(
                    EInputKind.N64_LEFT,
                    EInputKind.N64_RIGHT,
                    verticalMarginDP = 8f
                )
                PSP -> Config(
                    EInputKind.PSP_LEFT,
                    EInputKind.PSP_RIGHT
                )
                SNES -> Config(
                    EInputKind.SNES_LEFT,
                    EInputKind.SNES_RIGHT
                )
                GBA -> Config(
                    EInputKind.GBA_LEFT,
                    EInputKind.GBA_RIGHT
                )
                GENESIS_3 -> Config(
                    EInputKind.GENESIS_3_LEFT,
                    EInputKind.GENESIS_3_RIGHT
                )
                GENESIS_6 -> Config(
                    EInputKind.GENESIS_6_LEFT,
                    EInputKind.GENESIS_6_RIGHT,
                    1.0f,
                    1.2f
                )
                ATARI2600 -> Config(
                    EInputKind.ATARI2600_LEFT,
                    EInputKind.ATARI2600_RIGHT
                )
                SMS -> Config(
                    EInputKind.SMS_LEFT,
                    EInputKind.SMS_RIGHT
                )
                GG -> Config(
                    EInputKind.GG_LEFT,
                    EInputKind.GG_RIGHT
                )
                ARCADE_4 -> Config(
                    EInputKind.ARCADE_4_LEFT,
                    EInputKind.ARCADE_4_RIGHT
                )
                ARCADE_6 -> Config(
                    EInputKind.ARCADE_6_LEFT,
                    EInputKind.ARCADE_6_RIGHT,
                    1.0f,
                    1.2f
                )
                LYNX -> Config(
                    EInputKind.LYNX_LEFT,
                    EInputKind.LYNX_RIGHT
                )
                ATARI7800 -> Config(
                    EInputKind.ATARI7800_LEFT,
                    EInputKind.ATARI7800_RIGHT
                )
                PCE -> Config(
                    EInputKind.PCE_LEFT,
                    EInputKind.PCE_RIGHT
                )
                NGP -> Config(
                    EInputKind.NGP_LEFT,
                    EInputKind.NGP_RIGHT
                )
                DOS -> Config(
                    EInputKind.DOS_LEFT,
                    EInputKind.DOS_RIGHT
                )
                WS_LANDSCAPE -> Config(
                    EInputKind.WS_LANDSCAPE_LEFT,
                    EInputKind.WS_LANDSCAPE_RIGHT
                )
                WS_PORTRAIT -> Config(
                    EInputKind.WS_PORTRAIT_LEFT,
                    EInputKind.WS_PORTRAIT_RIGHT
                )
                NINTENDO_3DS -> Config(
                    EInputKind.NINTENDO_3DS_LEFT,
                    EInputKind.NINTENDO_3DS_RIGHT
                )
            }
        }
    }
}
