package com.mozhimen.emulatork.basic.controller.touch

import com.mozhimen.emulatork.input.InputKind

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
        val leftConfig: InputKind,
        val rightConfig: InputKind,
        val leftScale: Float = 1.0f,
        val rightScale: Float = 1.0f,
        val verticalMarginDP: Float = 0f
    )

    companion object {
        fun getConfig(id: ControllerTouchID): Config {
            return when (id) {
                GB -> Config(
                    InputKind.GB_LEFT,
                    InputKind.GB_RIGHT
                )
                NES -> Config(
                    InputKind.NES_LEFT,
                    InputKind.NES_RIGHT
                )
                DESMUME -> Config(
                    InputKind.DESMUME_LEFT,
                    InputKind.DESMUME_RIGHT
                )
                MELONDS -> Config(
                    InputKind.MELONDS_NDS_LEFT,
                    InputKind.MELONDS_NDS_RIGHT
                )
                PSX -> Config(
                    InputKind.PSX_LEFT,
                    InputKind.PSX_RIGHT
                )
                PSX_DUALSHOCK -> Config(
                    InputKind.PSX_DUALSHOCK_LEFT,
                    InputKind.PSX_DUALSHOCK_RIGHT
                )
                N64 -> Config(
                    InputKind.N64_LEFT,
                    InputKind.N64_RIGHT,
                    verticalMarginDP = 8f
                )
                PSP -> Config(
                    InputKind.PSP_LEFT,
                    InputKind.PSP_RIGHT
                )
                SNES -> Config(
                    InputKind.SNES_LEFT,
                    InputKind.SNES_RIGHT
                )
                GBA -> Config(
                    InputKind.GBA_LEFT,
                    InputKind.GBA_RIGHT
                )
                GENESIS_3 -> Config(
                    InputKind.GENESIS_3_LEFT,
                    InputKind.GENESIS_3_RIGHT
                )
                GENESIS_6 -> Config(
                    InputKind.GENESIS_6_LEFT,
                    InputKind.GENESIS_6_RIGHT,
                    1.0f,
                    1.2f
                )
                ATARI2600 -> Config(
                    InputKind.ATARI2600_LEFT,
                    InputKind.ATARI2600_RIGHT
                )
                SMS -> Config(
                    InputKind.SMS_LEFT,
                    InputKind.SMS_RIGHT
                )
                GG -> Config(
                    InputKind.GG_LEFT,
                    InputKind.GG_RIGHT
                )
                ARCADE_4 -> Config(
                    InputKind.ARCADE_4_LEFT,
                    InputKind.ARCADE_4_RIGHT
                )
                ARCADE_6 -> Config(
                    InputKind.ARCADE_6_LEFT,
                    InputKind.ARCADE_6_RIGHT,
                    1.0f,
                    1.2f
                )
                LYNX -> Config(
                    InputKind.LYNX_LEFT,
                    InputKind.LYNX_RIGHT
                )
                ATARI7800 -> Config(
                    InputKind.ATARI7800_LEFT,
                    InputKind.ATARI7800_RIGHT
                )
                PCE -> Config(
                    InputKind.PCE_LEFT,
                    InputKind.PCE_RIGHT
                )
                NGP -> Config(
                    InputKind.NGP_LEFT,
                    InputKind.NGP_RIGHT
                )
                DOS -> Config(
                    InputKind.DOS_LEFT,
                    InputKind.DOS_RIGHT
                )
                WS_LANDSCAPE -> Config(
                    InputKind.WS_LANDSCAPE_LEFT,
                    InputKind.WS_LANDSCAPE_RIGHT
                )
                WS_PORTRAIT -> Config(
                    InputKind.WS_PORTRAIT_LEFT,
                    InputKind.WS_PORTRAIT_RIGHT
                )
                NINTENDO_3DS -> Config(
                    InputKind.NINTENDO_3DS_LEFT,
                    InputKind.NINTENDO_3DS_RIGHT
                )
            }
        }
    }
}
