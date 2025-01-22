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

    fun getATARI_2600(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.ATARI2600,
            mergeDPADAndLeftStickEvents = true
        )

    fun getNES(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.NES,
            mergeDPADAndLeftStickEvents = true
        )

    fun getSNES(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.SNES,
            mergeDPADAndLeftStickEvents = true
        )

    fun getSMS(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.SMS,
            mergeDPADAndLeftStickEvents = true
        )

    fun getGENESIS_6(): GamepadConfig =
        GamepadConfig(
            "default_6",
            R.string.controller_genesis_6,
            EInputType.GENESIS_6,
            mergeDPADAndLeftStickEvents = true,
            libretroDescriptor = "MD Joypad 6 Button"
        )

    fun getGENESIS_3(): GamepadConfig =
        GamepadConfig(
            "default_3",
            R.string.controller_genesis_3,
            EInputType.GENESIS_3,
            mergeDPADAndLeftStickEvents = true,
            libretroDescriptor = "MD Joypad 3 Button"
        )

    fun getGG(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.GG,
            mergeDPADAndLeftStickEvents = true
        )

    fun getGB(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.GB,
            mergeDPADAndLeftStickEvents = true
        )

    fun getGBA(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.GBA,
            mergeDPADAndLeftStickEvents = true
        )

    fun getN64(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.N64,
            allowTouchRotation = true
        )

    fun getPSX_STANDARD(): GamepadConfig =
        GamepadConfig(
            "standard",
            R.string.controller_standard,
            EInputType.PSX,
            mergeDPADAndLeftStickEvents = true,
            libretroDescriptor = "standard"
        )

    fun getPSX_DUALSHOCK(): GamepadConfig =
        GamepadConfig(
            "dualshock",
            R.string.controller_dualshock,
            EInputType.PSX_DUALSHOCK,
            allowTouchRotation = true,
            libretroDescriptor = "dualshock"
        )

    fun getPSP(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.PSP,
            allowTouchRotation = true,
        )

    fun getFB_NEO_4(): GamepadConfig =
        GamepadConfig(
            "default_4",
            R.string.controller_arcade_4,
            EInputType.ARCADE_4,
            mergeDPADAndLeftStickEvents = true
        )

    fun getFB_NEO_6(): GamepadConfig =
        GamepadConfig(
            "default_6",
            R.string.controller_arcade_6,
            EInputType.ARCADE_6,
            mergeDPADAndLeftStickEvents = true
        )

    fun getMAME_2003_4(): GamepadConfig =
        GamepadConfig(
            "default_4",
            R.string.controller_arcade_4,
            EInputType.ARCADE_4,
            mergeDPADAndLeftStickEvents = true
        )

    fun getMAME_2003_6(): GamepadConfig =
        GamepadConfig(
            "default_6",
            R.string.controller_arcade_6,
            EInputType.ARCADE_6,
            mergeDPADAndLeftStickEvents = true
        )

    fun getDESMUME(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.DESMUME,
            allowTouchOverlay = false
        )

    fun getMELONDS(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.MELONDS,
            mergeDPADAndLeftStickEvents = true,
            allowTouchOverlay = false
        )

    fun getLYNX(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.LYNX,
            mergeDPADAndLeftStickEvents = true,
        )

    fun getATARI7800(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.ATARI7800,
            mergeDPADAndLeftStickEvents = true,
        )

    fun getPCE(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.PCE,
            mergeDPADAndLeftStickEvents = true,
        )

    fun getNGP(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.NGP,
            mergeDPADAndLeftStickEvents = true,
        )

    fun getDOS_AUTO(): GamepadConfig =
        GamepadConfig(
            "auto",
            R.string.controller_dos_auto,
            EInputType.DOS,
            allowTouchRotation = true,
            libretroId = 1
        )

    fun getDOS_MOUSE_LEFT(): GamepadConfig =
        GamepadConfig(
            "mouse_left",
            R.string.controller_dos_mouse_left,
            EInputType.DOS,
            allowTouchRotation = true,
            libretroId = 513
        )

    fun getDOS_MOUSE_RIGHT(): GamepadConfig =
        GamepadConfig(
            "mouse_right",
            R.string.controller_dos_mouse_right,
            EInputType.DOS,
            allowTouchRotation = true,
            libretroId = 769
        )

    fun getWS_LANDSCAPE(): GamepadConfig =
        GamepadConfig(
            "landscape",
            R.string.controller_landscape,
            EInputType.WS_LANDSCAPE,
            mergeDPADAndLeftStickEvents = true,
        )

    fun getWS_PORTRAIT(): GamepadConfig =
        GamepadConfig(
            "portrait",
            R.string.controller_portrait,
            EInputType.WS_PORTRAIT,
            mergeDPADAndLeftStickEvents = true,
        )

    fun getNINTENDO_3DS(): GamepadConfig =
        GamepadConfig(
            "default",
            R.string.controller_default,
            EInputType.NINTENDO_3DS,
            allowTouchOverlay = false
        )
}