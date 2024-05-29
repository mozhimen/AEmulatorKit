package com.mozhimen.emulatork.input.virtual.gamepad

import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.type.EInputType
/**
 * @ClassName ControllerConfigs
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
object GamepadConfigProvider {

    val ATARI_2600 = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.ATARI2600,
        mergeDPADAndLeftStickEvents = true
    )

    val NES = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.NES,
        mergeDPADAndLeftStickEvents = true
    )

    val SNES = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.SNES,
        mergeDPADAndLeftStickEvents = true
    )

    val SMS = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.SMS,
        mergeDPADAndLeftStickEvents = true
    )

    val GENESIS_6 = GamepadConfig(
        "default_6",
        R.string.controller_genesis_6,
        EInputType.GENESIS_6,
        mergeDPADAndLeftStickEvents = true,
        libretroDescriptor = "MD Joypad 6 Button"
    )

    val GENESIS_3 = GamepadConfig(
        "default_3",
        R.string.controller_genesis_3,
        EInputType.GENESIS_3,
        mergeDPADAndLeftStickEvents = true,
        libretroDescriptor = "MD Joypad 3 Button"
    )

    val GG = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.GG,
        mergeDPADAndLeftStickEvents = true
    )

    val GB = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.GB,
        mergeDPADAndLeftStickEvents = true
    )

    val GBA = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.GBA,
        mergeDPADAndLeftStickEvents = true
    )

    val N64 = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.N64,
        allowTouchRotation = true
    )

    val PSX_STANDARD = GamepadConfig(
        "standard",
        R.string.controller_standard,
        EInputType.PSX,
        mergeDPADAndLeftStickEvents = true,
        libretroDescriptor = "standard"
    )

    val PSX_DUALSHOCK = GamepadConfig(
        "dualshock",
        R.string.controller_dualshock,
        EInputType.PSX_DUALSHOCK,
        allowTouchRotation = true,
        libretroDescriptor = "dualshock"
    )

    val PSP = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.PSP,
        allowTouchRotation = true,
    )

    val FB_NEO_4 = GamepadConfig(
        "default_4",
        R.string.controller_arcade_4,
        EInputType.ARCADE_4,
        mergeDPADAndLeftStickEvents = true
    )

    val FB_NEO_6 = GamepadConfig(
        "default_6",
        R.string.controller_arcade_6,
        EInputType.ARCADE_6,
        mergeDPADAndLeftStickEvents = true
    )

    val MAME_2003_4 = GamepadConfig(
        "default_4",
        R.string.controller_arcade_4,
        EInputType.ARCADE_4,
        mergeDPADAndLeftStickEvents = true
    )

    val MAME_2003_6 = GamepadConfig(
        "default_6",
        R.string.controller_arcade_6,
        EInputType.ARCADE_6,
        mergeDPADAndLeftStickEvents = true
    )

    val DESMUME = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.DESMUME,
        allowTouchOverlay = false
    )

    val MELONDS = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.MELONDS,
        mergeDPADAndLeftStickEvents = true,
        allowTouchOverlay = false
    )

    val LYNX = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.LYNX,
        mergeDPADAndLeftStickEvents = true,
    )

    val ATARI7800 = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.ATARI7800,
        mergeDPADAndLeftStickEvents = true,
    )

    val PCE = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.PCE,
        mergeDPADAndLeftStickEvents = true,
    )

    val NGP = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.NGP,
        mergeDPADAndLeftStickEvents = true,
    )

    val DOS_AUTO = GamepadConfig(
        "auto",
        R.string.controller_dos_auto,
        EInputType.DOS,
        allowTouchRotation = true,
        libretroId = 1
    )

    val DOS_MOUSE_LEFT = GamepadConfig(
        "mouse_left",
        R.string.controller_dos_mouse_left,
        EInputType.DOS,
        allowTouchRotation = true,
        libretroId = 513
    )

    val DOS_MOUSE_RIGHT = GamepadConfig(
        "mouse_right",
        R.string.controller_dos_mouse_right,
        EInputType.DOS,
        allowTouchRotation = true,
        libretroId = 769
    )

    val WS_LANDSCAPE = GamepadConfig(
        "landscape",
        R.string.controller_landscape,
        EInputType.WS_LANDSCAPE,
        mergeDPADAndLeftStickEvents = true,
    )

    val WS_PORTRAIT = GamepadConfig(
        "portrait",
        R.string.controller_portrait,
        EInputType.WS_PORTRAIT,
        mergeDPADAndLeftStickEvents = true,
    )

    val NINTENDO_3DS = GamepadConfig(
        "default",
        R.string.controller_default,
        EInputType.NINTENDO_3DS,
        allowTouchOverlay = false
    )
}