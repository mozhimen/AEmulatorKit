package com.mozhimen.emulatork.input.config

import android.os.Build
import android.view.KeyEvent
import com.mozhimen.emulatork.input.R
import com.mozhimen.emulatork.input.kind.EInputKind
import com.swordfish.radialgamepad.library.config.ButtonConfig
import com.swordfish.radialgamepad.library.config.CrossConfig
import com.swordfish.radialgamepad.library.config.CrossContentDescription
import com.swordfish.radialgamepad.library.config.PrimaryDialConfig
import com.swordfish.radialgamepad.library.config.RadialGamePadConfig
import com.swordfish.radialgamepad.library.config.RadialGamePadTheme
import com.swordfish.radialgamepad.library.config.SecondaryDialConfig
import com.swordfish.radialgamepad.library.event.GestureType
import com.swordfish.radialgamepad.library.haptics.HapticConfig

/**
 * @ClassName InputConfigRadialGamePadProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/28
 * @Version 1.0
 */
object InputConfigRadialGamePadProvider {
    const val DEFAULT_STICK_ROTATION = 8f
    const val MOTION_SOURCE_DPAD = 0
    const val MOTION_SOURCE_LEFT_STICK = 1
    const val MOTION_SOURCE_RIGHT_STICK = 2
    const val MOTION_SOURCE_DPAD_AND_LEFT_STICK = 3
    const val MOTION_SOURCE_RIGHT_DPAD = 4

    ///////////////////////////////////////////////////////////////////////////////////////

    @JvmStatic
    fun getRadialGamePadConfig(
        kind: EInputKind,
        haptic: HapticConfig,
        theme: RadialGamePadTheme
    ): RadialGamePadConfig {
        val radialGamePadConfig = when (kind) {
            EInputKind.GB_LEFT -> getGBLeft(theme)
            EInputKind.GB_RIGHT -> getGBRight(theme)
            EInputKind.NES_LEFT -> getNESLeft(theme)
            EInputKind.NES_RIGHT -> getNESRight(theme)
            EInputKind.DESMUME_LEFT -> getDesmumeLeft(theme)
            EInputKind.DESMUME_RIGHT -> getDesmumeRight(theme)
            EInputKind.MELONDS_NDS_LEFT -> getMelondsLeft(theme)
            EInputKind.MELONDS_NDS_RIGHT -> getMelondsRight(theme)
            EInputKind.PSX_LEFT -> getPSXLeft(theme)
            EInputKind.PSX_RIGHT -> getPSXRight(theme)
            EInputKind.PSX_DUALSHOCK_LEFT -> getPSXDualshockLeft(theme)
            EInputKind.PSX_DUALSHOCK_RIGHT -> getPSXDualshockRight(theme)
            EInputKind.PSP_LEFT -> getPSPLeft(theme)
            EInputKind.PSP_RIGHT -> getPSPRight(theme)
            EInputKind.SNES_LEFT -> getSNESLeft(theme)
            EInputKind.SNES_RIGHT -> getSNESRight(theme)
            EInputKind.GBA_LEFT -> getGBALeft(theme)
            EInputKind.GBA_RIGHT -> getGBARight(theme)
            EInputKind.SMS_LEFT -> getSMSLeft(theme)
            EInputKind.SMS_RIGHT -> getSMSRight(theme)
            EInputKind.GG_LEFT -> getGGLeft(theme)
            EInputKind.GG_RIGHT -> getGGRight(theme)
            EInputKind.LYNX_LEFT -> getLynxLeft(theme)
            EInputKind.LYNX_RIGHT -> getLynxRight(theme)
            EInputKind.PCE_LEFT -> getPCELeft(theme)
            EInputKind.PCE_RIGHT -> getPCERight(theme)
            EInputKind.DOS_LEFT -> getDOSLeft(theme)
            EInputKind.DOS_RIGHT -> getDOSRight(theme)
            EInputKind.NGP_LEFT -> getNGPLeft(theme)
            EInputKind.NGP_RIGHT -> getNGPRight(theme)
            EInputKind.WS_LANDSCAPE_LEFT -> getWSLandscapeLeft(theme)
            EInputKind.WS_LANDSCAPE_RIGHT -> getWSLandscapeRight(theme)
            EInputKind.WS_PORTRAIT_LEFT -> getWSPortraitLeft(theme)
            EInputKind.WS_PORTRAIT_RIGHT -> getWSPortraitRight(theme)
            EInputKind.N64_LEFT -> getN64Left(theme)
            EInputKind.N64_RIGHT -> getN64Right(theme)
            EInputKind.GENESIS_3_LEFT -> getGenesis3Left(theme)
            EInputKind.GENESIS_3_RIGHT -> getGenesis3Right(theme)
            EInputKind.GENESIS_6_LEFT -> getGenesis6Left(theme)
            EInputKind.GENESIS_6_RIGHT -> getGenesis6Right(theme)
            EInputKind.ATARI2600_LEFT -> getAtari2600Left(theme)
            EInputKind.ATARI2600_RIGHT -> getAtari2600Right(theme)
            EInputKind.ARCADE_4_LEFT -> getArcade4Left(theme)
            EInputKind.ARCADE_4_RIGHT -> getArcade4Right(theme)
            EInputKind.ARCADE_6_LEFT -> getArcade6Left(theme)
            EInputKind.ARCADE_6_RIGHT -> getArcade6Right(theme)
            EInputKind.ATARI7800_LEFT -> getAtari7800Left(theme)
            EInputKind.ATARI7800_RIGHT -> getAtari7800Right(theme)
            EInputKind.NINTENDO_3DS_LEFT -> getNintendo3DSLeft(theme)
            EInputKind.NINTENDO_3DS_RIGHT -> getNintendo3DSRight(theme)
        }

        // Using standard MotionEvents.getX/Y() is broken on Samsung devices running Android 11,
        // but Android 13 broke MotionEvents.getRawX/Y() events in landscape mode for every device.
        val useRawTouchCoordinates = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

        return radialGamePadConfig.copy(
            haptic = haptic,
            preferScreenTouchCoordinates = useRawTouchCoordinates
        )
    }
    
    fun getGBLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getGBRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                ),
                rotationInDegrees = 30f
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getNESLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getNESRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getDesmumeLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(
                    8,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_THUMBL,
                        iconId = R.drawable.button_mic,
                        contentDescription = "Microphone"
                    )
                ),
                SecondaryDialConfig.SingleButton(
                    10,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L2,
                        iconId = R.drawable.button_close_screen,
                        contentDescription = "Close"
                    )
                ),
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getL())
            )
        )

    fun getDesmumeRight(theme: RadialGamePadTheme) = RadialGamePadConfig(
        theme = theme,
        sockets = 12,
        primaryDial = PrimaryDialConfig.PrimaryButtons(
            dials = listOf(
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_A,
                    label = "A"
                ),
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_X,
                    label = "X"
                ),
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_Y,
                    label = "Y"
                ),
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_B,
                    label = "B"
                )
            )
        ),
        secondaryDials = listOf(
            SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getR()),
            SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
            InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
        )
    )

    fun getMelondsLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(
                    8,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L2,
                        iconId = R.drawable.button_mic,
                        contentDescription = "Microphone"
                    )
                ),
                SecondaryDialConfig.SingleButton(
                    10,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_THUMBL,
                        iconId = R.drawable.button_close_screen,
                        contentDescription = "Close"
                    )
                ),
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getL())
            )
        )

    fun getMelondsRight(theme: RadialGamePadTheme) = RadialGamePadConfig(
        theme = theme,
        sockets = 12,
        primaryDial = PrimaryDialConfig.PrimaryButtons(
            dials = listOf(
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_A,
                    label = "A"
                ),
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_X,
                    label = "X"
                ),
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_Y,
                    label = "Y"
                ),
                ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_B,
                    label = "B"
                )
            )
        ),
        secondaryDials = listOf(
            SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getR()),
            SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
            InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
        )
    )

    fun getPSXLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getL1()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getL2()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getPSXRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                listOf(
                    InputConfigButtonProvider.getCIRCLE(),
                    InputConfigButtonProvider.getTRIANGLE(),
                    InputConfigButtonProvider.getSQUARE(),
                    InputConfigButtonProvider.getCROSS()
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getR2()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getR1()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getPSXDualshockLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getL1()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getL2()),
                SecondaryDialConfig.Stick(
                    9,
                    2,
                    2.2f,
                    0f,
                    InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK,
                    KeyEvent.KEYCODE_BUTTON_THUMBL,
                    contentDescription = "Left Stick",
                    supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(-DEFAULT_STICK_ROTATION)
                ),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getPSXDualshockRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                listOf(
                    InputConfigButtonProvider.getCIRCLE(),
                    InputConfigButtonProvider.getTRIANGLE(),
                    InputConfigButtonProvider.getSQUARE(),
                    InputConfigButtonProvider.getCROSS()
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getR2()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getR1()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme),
                SecondaryDialConfig.Stick(
                    8,
                    2,
                    2.2f,
                    0f,
                    InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_STICK,
                    KeyEvent.KEYCODE_BUTTON_THUMBR,
                    contentDescription = "Right Stick",
                    supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(DEFAULT_STICK_ROTATION)
                )
            )
        )

    fun getN64Left(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(
                    2,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L2,
                        label = "Z"
                    )
                ),
                SecondaryDialConfig.DoubleButton(3, 0f, InputConfigButtonProvider.getL()),
                SecondaryDialConfig.Stick(
                    9,
                    2,
                    2.2f,
                    0.1f,
                    InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK,
                    supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(-DEFAULT_STICK_ROTATION)
                ),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getN64Right(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L2,
                        label = "Z"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        label = "B"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "A"
                    )
                ),
                rotationInDegrees = 60f
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.DoubleButton(2, 0f, InputConfigButtonProvider.getR()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                SecondaryDialConfig.SingleButton(
                    index = 10,
                    scale = 1f,
                    distance = -0.1f,
                    buttonConfig = InputConfigButtonProvider.getMENU(),
                    rotationProcessor = object : SecondaryDialConfig.RotationProcessor() {
                        override fun getRotation(rotation: Float): Float {
                            return -rotation
                        }
                    },
                    theme = theme
                ),
                SecondaryDialConfig.Cross(
                    8,
                    2,
                    2.2f,
                    0.1f,
                    CrossConfig(
                        id = InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_DPAD,
                        shape = CrossConfig.Shape.CIRCLE,
                        rightDrawableForegroundId = R.drawable.direction_alt_foreground,
                        contentDescription = CrossContentDescription(
                            baseName = "c"
                        ),
                        supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                        useDiagonals = false,
                    ),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(DEFAULT_STICK_ROTATION)
                )
            )
        )

    fun getPSPLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.DoubleButton(3, 0f, InputConfigButtonProvider.getL()),
                SecondaryDialConfig.Stick(
                    9,
                    2,
                    2.2f,
                    0f,
                    InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK,
                    supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(-DEFAULT_STICK_ROTATION)
                ),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getPSPRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                listOf(
                    InputConfigButtonProvider.getCIRCLE(),
                    InputConfigButtonProvider.getTRIANGLE(),
                    InputConfigButtonProvider.getSQUARE(),
                    InputConfigButtonProvider.getCROSS()
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.DoubleButton(2, 0f, InputConfigButtonProvider.getR()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme),
                SecondaryDialConfig.Empty(
                    8,
                    2,
                    2.2f,
                    0f,
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(DEFAULT_STICK_ROTATION)
                )
            )
        )

    fun getSNESLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.DoubleButton(3, 0f, InputConfigButtonProvider.getL()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getSNESRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_X,
                        label = "X"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        label = "Y"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.DoubleButton(2, 0f, InputConfigButtonProvider.getR()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getGBALeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.DoubleButton(3, 0f, InputConfigButtonProvider.getL()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getGBARight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                ),
                rotationInDegrees = 30f
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.DoubleButton(2, 0f, InputConfigButtonProvider.getR()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getGenesis3Left(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getGenesis3Right(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "C"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        label = "A"
                    ),
                ),
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getGenesis6Left(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(8, theme),
                SecondaryDialConfig.Empty(9, 1, 1f, 0f)
            )
        )

    fun getGenesis6Right(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "C"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_R1,
                        label = "Z"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_X,
                        label = "Y"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L1,
                        label = "X"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_UNKNOWN,
                        visible = false
                    )
                ),
                center = ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_B,
                    label = "B"
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.Empty(9, 1, 0.5f, 0f),
                SecondaryDialConfig.Empty(3, 1, 0.5f, 0f)
            )
        )

    fun getAtari2600Left(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(
                    4,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L1,
                        label = "DIFF.A"
                    )
                ),
                SecondaryDialConfig.SingleButton(
                    2,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L2,
                        label = "DIFF.B"
                    )
                ),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getAtari2600Right(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(),
                center = ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_B,
                    contentDescription = "Action"
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(
                    2,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_START,
                        label = "RESET"
                    )
                ),
                SecondaryDialConfig.SingleButton(
                    4,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_SELECT,
                        label = "SELECT"
                    )
                ),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getSMSLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.Empty(4, 1, 1f, 0f),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getSMSRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "2"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "1"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getGGLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.Empty(4, 1, 1f, 0f),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getGGRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "2"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "1"
                    )
                ),
                rotationInDegrees = 30f
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getArcade4Left(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS_MERGED(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getCOIN()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getArcade4Right(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                rotationInDegrees = 60f,
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_X,
                        contentDescription = "X",
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        contentDescription = "Y",
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        contentDescription = "B",
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        contentDescription = "A",
                    )
                ),
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getArcade6Left(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS_MERGED(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getCOIN()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(8, theme),
                SecondaryDialConfig.Empty(9, 1, 1f, 0f)
            )
        )

    fun getArcade6Right(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_R1,
                        contentDescription = "R1"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L1,
                        contentDescription = "L1"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_X,
                        contentDescription = "X"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        contentDescription = "Y"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        contentDescription = "B"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_UNKNOWN,
                        visible = false
                    )
                ),
                center = ButtonConfig(
                    id = KeyEvent.KEYCODE_BUTTON_A
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.Empty(9, 1, 0.5f, 0f),
                SecondaryDialConfig.Empty(3, 1, 0.5f, 0f)
            )
        )

    fun getLynxLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(
                    4,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_L1,
                        label = "OPTION 1"
                    )
                ),
                SecondaryDialConfig.SingleButton(
                    8,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_R1,
                        label = "OPTION 2"
                    )
                ),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getLynxRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                rotationInDegrees = 15f,
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getAtari7800Left(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getAtari7800Right(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "2"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "1"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getPCELeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getPCERight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "II"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "I"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getDOSLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getL1()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getL2()),
                SecondaryDialConfig.SingleButton(
                    8,
                    1f,
                    0f,
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_THUMBL,
                        iconId = R.drawable.button_keyboard,
                        contentDescription = "Keyboard"
                    ),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationInvert(),
                ),
                SecondaryDialConfig.Stick(
                    9,
                    2,
                    2.2f,
                    0f,
                    InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK,
                    contentDescription = "Left Stick",
                    supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(-DEFAULT_STICK_ROTATION)
                )
            )
        )

    fun getDOSRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_X,
                        label = "X"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        label = "Y"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getR2()),
                SecondaryDialConfig.SingleButton(3, 1f, 0f, InputConfigButtonProvider.getR1()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme),
                SecondaryDialConfig.Stick(
                    8,
                    2,
                    2.2f,
                    0f,
                    InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_STICK,
                    KeyEvent.KEYCODE_BUTTON_THUMBR,
                    contentDescription = "Right Stick",
                    supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(DEFAULT_STICK_ROTATION)
                )
            )
        )

    fun getNGPLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.Empty(4, 1, 1f, 0f),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getNGPRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "B"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "A"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getWSLandscapeLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.Empty(4, 1, 1f, 0f),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getWSLandscapeRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                rotationInDegrees = 30f,
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getWSPortraitLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.Empty(4, 1, 1f, 0f),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getWSPortraitRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "X3"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_X,
                        label = "X2"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        label = "X1"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "X4"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme)
            )
        )

    fun getNintendo3DSLeft(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = InputConfigPrimaryDialProvider.getCROSS(),
            secondaryDials = listOf(
                SecondaryDialConfig.SingleButton(2, 1f, 0f, InputConfigButtonProvider.getSELECT()),
                SecondaryDialConfig.DoubleButton(3, 0f, InputConfigButtonProvider.getL()),
                SecondaryDialConfig.Stick(
                    9,
                    2,
                    2.2f,
                    0f,
                    InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK,
                    supportsGestures = setOf(GestureType.TRIPLE_TAP, GestureType.FIRST_TOUCH),
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(-DEFAULT_STICK_ROTATION)
                ),
                SecondaryDialConfig.Empty(8, 1, 1f, 0f)
            )
        )

    fun getNintendo3DSRight(theme: RadialGamePadTheme) =
        RadialGamePadConfig(
            theme = theme,
            sockets = 12,
            primaryDial = PrimaryDialConfig.PrimaryButtons(
                dials = listOf(
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_A,
                        label = "A"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_X,
                        label = "X"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_Y,
                        label = "Y"
                    ),
                    ButtonConfig(
                        id = KeyEvent.KEYCODE_BUTTON_B,
                        label = "B"
                    )
                )
            ),
            secondaryDials = listOf(
                SecondaryDialConfig.DoubleButton(2, 0f, InputConfigButtonProvider.getR()),
                SecondaryDialConfig.SingleButton(4, 1f, 0f, InputConfigButtonProvider.getSTART()),
                InputConfigSecondaryDialProvider.getSingleButtonMenu(10, theme),
                SecondaryDialConfig.Empty(
                    8,
                    2,
                    2.2f,
                    0f,
                    rotationProcessor = InputConfigSecondaryDialProvider.getRotationProcessorRotationOffset(DEFAULT_STICK_ROTATION)
                )
            )
        )

}