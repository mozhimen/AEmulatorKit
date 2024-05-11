package com.mozhimen.emulatork.basic.library

/**
 * @ClassName SystemID
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
enum class SystemID(val dbname: String) {
    NES("nes"),
    SNES("snes"),
    GENESIS("md"),
    GB("gb"),
    GBC("gbc"),
    GBA("gba"),
    N64("n64"),
    SMS("sms"),
    PSP("psp"),
    NDS("nds"),
    GG("gg"),
    ATARI2600("atari2600"),
    PSX("psx"),
    FBNEO("fbneo"),
    MAME2003PLUS("mame2003plus"),
    PC_ENGINE("pce"),
    LYNX("lynx"),
    ATARI7800("atari7800"),
    SEGACD("scd"),
    NGP("ngp"),
    NGC("ngc"),
    WS("ws"),
    WSC("wsc"),
    DOS("dos"),
    NINTENDO_3DS("3ds"),
}
