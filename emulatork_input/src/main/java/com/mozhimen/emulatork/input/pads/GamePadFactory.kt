package com.mozhimen.emulatork.input.pads

import android.content.Context

/**
 * @ClassName GamePadFactory
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class GamePadFactory {
    enum class Layout {
        NES,
        SNES,
        GBA,
        GENESIS,
        N64,
        PSX
    }

    companion object {
        fun getGamePadView(context: Context, layout: Layout): BaseGamePad {
            return when (layout) {
                Layout.NES -> GameBoyPad(context)
                Layout.SNES -> SNESPad(context)
                Layout.GENESIS -> GenesisPad(context)
                Layout.GBA -> GameBoyAdvancePad(context)
                Layout.PSX -> PSXPad(context)
                Layout.N64 -> N64Pad(context)
            }
        }
    }
}