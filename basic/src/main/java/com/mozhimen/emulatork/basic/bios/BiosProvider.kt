package com.mozhimen.emulatork.basic.bios

import com.mozhimen.emulatork.basic.system.ESystemType

/**
 * @ClassName BiosProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/30
 * @Version 1.0
 */
object BiosProvider {

    private val BIOSS: List<Bios> by lazy { listOf(getPsOne45(), getPsOriginal41(), getPsOriginal3(), getPsOriginal22(), getLynxBoot(), getSegaCdE(), getSegaCdJ(), getSegaCdU(), getNintendoDsArm7(), getNintendoDsArm9(), getNintendoDsFirmware()) }

    /////////////////////////////////////////////////////////////////////////////

    fun getBioss(): List<Bios> =
        BIOSS

    fun getPsOne45() = Bios(
        "scph101.bin",
        "6E3735FF4C7DC899EE98981385F6F3D0",
        "PS One 4.5 NTSC-U/C",
        ESystemType.PSX,
        "171BDCEC"
    )

    fun getPsOriginal41() = Bios(
        "scph7001.bin",
        "1E68C231D0896B7EADCAD1D7D8E76129",
        "PS Original 4.1 NTSC-U/C",
        ESystemType.PSX,
        "502224B6"
    )

    fun getPsOriginal3() = Bios(
        "scph5501.bin",
        "490F666E1AFB15B7362B406ED1CEA246",
        "PS Original 3.0 NTSC-U/C",
        ESystemType.PSX,
        "8D8CB7E4"
    )

    fun getPsOriginal22() = Bios(
        "scph1001.bin",
        "924E392ED05558FFDB115408C263DCCF",
        "PS Original 2.2 NTSC-U/C",
        ESystemType.PSX,
        "37157331"
    )

    fun getLynxBoot() = Bios(
        "lynxboot.img",
        "FCD403DB69F54290B51035D82F835E7B",
        "Lynx Boot Image",
        ESystemType.LYNX,
        "0D973C9D"
    )

    fun getSegaCdE() = Bios(
        "bios_CD_E.bin",
        "E66FA1DC5820D254611FDCDBA0662372",
        "Sega CD E",
        ESystemType.SEGACD,
        "529AC15A"
    )

    fun getSegaCdJ() = Bios(
        "bios_CD_J.bin",
        "278A9397D192149E84E820AC621A8EDD",
        "Sega CD J",
        ESystemType.SEGACD,
        "9D2DA8F2"
    )

    fun getSegaCdU() = Bios(
        "bios_CD_U.bin",
        "2EFD74E3232FF260E371B99F84024F7F",
        "Sega CD U",
        ESystemType.SEGACD,
        "C6D10268"
    )

    fun getNintendoDsArm7() = Bios(
        "bios7.bin",
        "DF692A80A5B1BC90728BC3DFC76CD948",
        "Nintendo DS ARM7",
        ESystemType.NDS,
        "1280F0D5"
    )

    fun getNintendoDsArm9() = Bios(
        "bios9.bin",
        "A392174EB3E572FED6447E956BDE4B25",
        "Nintendo DS ARM9",
        ESystemType.NDS,
        "2AB23573"
    )

    fun getNintendoDsFirmware() = Bios(
        "firmware.bin",
        "E45033D9B0FA6B0DE071292BBA7C9D13",
        "Nintendo DS Firmware",
        ESystemType.NDS,
        "945F9DC9",
        "nds_firmware.bin"
    )
}