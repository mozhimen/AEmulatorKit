package com.mozhimen.emulatork.input.virtual.area

import com.mozhimen.emulatork.input.type.EInputType

/**
 * @ClassName GamepadConfig
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/29
 * @Version 1.0
 */
class InputAreaConfig(
    val inputAreaLeft: EInputArea,
    val inputAreaRight: EInputArea,
    val scaleLeft: Float = 1.0f,
    val scaleRight: Float = 1.0f,
    val verticalMarginDP: Float = 0f
) {
    companion object {
        @JvmStatic
        fun getConfig(id: EInputType): InputAreaConfig =
            when (id) {
                EInputType.GB -> InputAreaConfig(
                    EInputArea.GB_LEFT,
                    EInputArea.GB_RIGHT
                )

                EInputType.NES -> InputAreaConfig(
                    EInputArea.NES_LEFT,
                    EInputArea.NES_RIGHT
                )

                EInputType.DESMUME -> InputAreaConfig(
                    EInputArea.DESMUME_LEFT,
                    EInputArea.DESMUME_RIGHT
                )

                EInputType.MELONDS -> InputAreaConfig(
                    EInputArea.MELONDS_NDS_LEFT,
                    EInputArea.MELONDS_NDS_RIGHT
                )

                EInputType.PSX -> InputAreaConfig(
                    EInputArea.PSX_LEFT,
                    EInputArea.PSX_RIGHT
                )

                EInputType.PSX_DUALSHOCK -> InputAreaConfig(
                    EInputArea.PSX_DUALSHOCK_LEFT,
                    EInputArea.PSX_DUALSHOCK_RIGHT
                )

                EInputType.N64 -> InputAreaConfig(
                    EInputArea.N64_LEFT,
                    EInputArea.N64_RIGHT,
                    verticalMarginDP = 8f
                )

                EInputType.PSP -> InputAreaConfig(
                    EInputArea.PSP_LEFT,
                    EInputArea.PSP_RIGHT
                )

                EInputType.SNES -> InputAreaConfig(
                    EInputArea.SNES_LEFT,
                    EInputArea.SNES_RIGHT
                )

                EInputType.GBA -> InputAreaConfig(
                    EInputArea.GBA_LEFT,
                    EInputArea.GBA_RIGHT
                )

                EInputType.GENESIS_3 -> InputAreaConfig(
                    EInputArea.GENESIS_3_LEFT,
                    EInputArea.GENESIS_3_RIGHT
                )

                EInputType.GENESIS_6 -> InputAreaConfig(
                    EInputArea.GENESIS_6_LEFT,
                    EInputArea.GENESIS_6_RIGHT,
                    1.0f,
                    1.2f
                )

                EInputType.ATARI2600 -> InputAreaConfig(
                    EInputArea.ATARI2600_LEFT,
                    EInputArea.ATARI2600_RIGHT
                )

                EInputType.SMS -> InputAreaConfig(
                    EInputArea.SMS_LEFT,
                    EInputArea.SMS_RIGHT
                )

                EInputType.GG -> InputAreaConfig(
                    EInputArea.GG_LEFT,
                    EInputArea.GG_RIGHT
                )

                EInputType.ARCADE_4 -> InputAreaConfig(
                    EInputArea.ARCADE_4_LEFT,
                    EInputArea.ARCADE_4_RIGHT
                )

                EInputType.ARCADE_6 -> InputAreaConfig(
                    EInputArea.ARCADE_6_LEFT,
                    EInputArea.ARCADE_6_RIGHT,
                    1.0f,
                    1.2f
                )

                EInputType.LYNX -> InputAreaConfig(
                    EInputArea.LYNX_LEFT,
                    EInputArea.LYNX_RIGHT
                )

                EInputType.ATARI7800 -> InputAreaConfig(
                    EInputArea.ATARI7800_LEFT,
                    EInputArea.ATARI7800_RIGHT
                )

                EInputType.PCE -> InputAreaConfig(
                    EInputArea.PCE_LEFT,
                    EInputArea.PCE_RIGHT
                )

                EInputType.NGP -> InputAreaConfig(
                    EInputArea.NGP_LEFT,
                    EInputArea.NGP_RIGHT
                )

                EInputType.DOS -> InputAreaConfig(
                    EInputArea.DOS_LEFT,
                    EInputArea.DOS_RIGHT
                )

                EInputType.WS_LANDSCAPE -> InputAreaConfig(
                    EInputArea.WS_LANDSCAPE_LEFT,
                    EInputArea.WS_LANDSCAPE_RIGHT
                )

                EInputType.WS_PORTRAIT -> InputAreaConfig(
                    EInputArea.WS_PORTRAIT_LEFT,
                    EInputArea.WS_PORTRAIT_RIGHT
                )

                EInputType.NINTENDO_3DS -> InputAreaConfig(
                    EInputArea.NINTENDO_3DS_LEFT,
                    EInputArea.NINTENDO_3DS_RIGHT
                )
            }
    }
}