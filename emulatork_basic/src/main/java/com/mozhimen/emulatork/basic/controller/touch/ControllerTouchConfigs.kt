package com.mozhimen.emulatork.basic.controller.touch

import com.mozhimen.emulatork.basic.R

/**
 * @ClassName ControllerConfigs
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
object ControllerTouchConfigs {

    val ATARI_2600 = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.ATARI2600,
        mergeDPADAndLeftStickEvents = true
    )

    val NES = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.NES,
        mergeDPADAndLeftStickEvents = true
    )

    val SNES = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.SNES,
        mergeDPADAndLeftStickEvents = true
    )

    val SMS = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.SMS,
        mergeDPADAndLeftStickEvents = true
    )

    val GENESIS_6 = ControllerTouchConfig(
        "default_6",
        R.string.controller_genesis_6,
        ControllerTouchID.GENESIS_6,
        mergeDPADAndLeftStickEvents = true,
        libretroDescriptor = "MD Joypad 6 Button"
    )

    val GENESIS_3 = ControllerTouchConfig(
        "default_3",
        R.string.controller_genesis_3,
        ControllerTouchID.GENESIS_3,
        mergeDPADAndLeftStickEvents = true,
        libretroDescriptor = "MD Joypad 3 Button"
    )

    val GG = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.GG,
        mergeDPADAndLeftStickEvents = true
    )

    val GB = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.GB,
        mergeDPADAndLeftStickEvents = true
    )

    val GBA = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.GBA,
        mergeDPADAndLeftStickEvents = true
    )

    val N64 = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.N64,
        allowTouchRotation = true
    )

    val PSX_STANDARD = ControllerTouchConfig(
        "standard",
        R.string.controller_standard,
        ControllerTouchID.PSX,
        mergeDPADAndLeftStickEvents = true,
        libretroDescriptor = "standard"
    )

    val PSX_DUALSHOCK = ControllerTouchConfig(
        "dualshock",
        R.string.controller_dualshock,
        ControllerTouchID.PSX_DUALSHOCK,
        allowTouchRotation = true,
        libretroDescriptor = "dualshock"
    )

    val PSP = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.PSP,
        allowTouchRotation = true,
    )

    val FB_NEO_4 = ControllerTouchConfig(
        "default_4",
        R.string.controller_arcade_4,
        ControllerTouchID.ARCADE_4,
        mergeDPADAndLeftStickEvents = true
    )

    val FB_NEO_6 = ControllerTouchConfig(
        "default_6",
        R.string.controller_arcade_6,
        ControllerTouchID.ARCADE_6,
        mergeDPADAndLeftStickEvents = true
    )

    val MAME_2003_4 = ControllerTouchConfig(
        "default_4",
        R.string.controller_arcade_4,
        ControllerTouchID.ARCADE_4,
        mergeDPADAndLeftStickEvents = true
    )

    val MAME_2003_6 = ControllerTouchConfig(
        "default_6",
        R.string.controller_arcade_6,
        ControllerTouchID.ARCADE_6,
        mergeDPADAndLeftStickEvents = true
    )

    val DESMUME = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.DESMUME,
        allowTouchOverlay = false
    )

    val MELONDS = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.MELONDS,
        mergeDPADAndLeftStickEvents = true,
        allowTouchOverlay = false
    )

    val LYNX = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.LYNX,
        mergeDPADAndLeftStickEvents = true,
    )

    val ATARI7800 = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.ATARI7800,
        mergeDPADAndLeftStickEvents = true,
    )

    val PCE = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.PCE,
        mergeDPADAndLeftStickEvents = true,
    )

    val NGP = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.NGP,
        mergeDPADAndLeftStickEvents = true,
    )

    val DOS_AUTO = ControllerTouchConfig(
        "auto",
        R.string.controller_dos_auto,
        ControllerTouchID.DOS,
        allowTouchRotation = true,
        libretroId = 1
    )

    val DOS_MOUSE_LEFT = ControllerTouchConfig(
        "mouse_left",
        R.string.controller_dos_mouse_left,
        ControllerTouchID.DOS,
        allowTouchRotation = true,
        libretroId = 513
    )

    val DOS_MOUSE_RIGHT = ControllerTouchConfig(
        "mouse_right",
        R.string.controller_dos_mouse_right,
        ControllerTouchID.DOS,
        allowTouchRotation = true,
        libretroId = 769
    )

    val WS_LANDSCAPE = ControllerTouchConfig(
        "landscape",
        R.string.controller_landscape,
        ControllerTouchID.WS_LANDSCAPE,
        mergeDPADAndLeftStickEvents = true,
    )

    val WS_PORTRAIT = ControllerTouchConfig(
        "portrait",
        R.string.controller_portrait,
        ControllerTouchID.WS_PORTRAIT,
        mergeDPADAndLeftStickEvents = true,
    )

    val NINTENDO_3DS = ControllerTouchConfig(
        "default",
        R.string.controller_default,
        ControllerTouchID.NINTENDO_3DS,
        allowTouchOverlay = false
    )
}