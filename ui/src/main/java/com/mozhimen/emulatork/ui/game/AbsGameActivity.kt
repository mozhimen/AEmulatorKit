package com.mozhimen.emulatork.ui.game

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.mozhimen.basick.utilk.android.util.dp2px
import com.mozhimen.basick.utilk.androidx.lifecycle.runOnLifecycleState
import com.mozhimen.basick.utilk.kotlin.UtilKBoolean
import com.mozhimen.basick.utilk.kotlinx.coroutines.collectSafe
import com.mozhimen.basick.elemk.mos.NTuple2
import com.mozhimen.basick.elemk.mos.NTuple3
import com.mozhimen.basick.utilk.kotlin.math.UtilKMathInterpolation
import com.mozhimen.basick.utilk.kotlinx.coroutines.batch_ofTime
import com.mozhimen.emulatork.ext.game.AbsGameActivity
import com.mozhimen.emulatork.ext.input.VirtualLongPressHandler
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.input.virtual.gyro.Gyro
import com.mozhimen.emulatork.input.virtual.gyro.GyroStick
import com.mozhimen.emulatork.input.virtual.gyro.GyroCross
import com.mozhimen.emulatork.input.virtual.gyro.GyroTwoButtons
import com.mozhimen.emulatork.input.config.InputConfigRadialGamePadProvider
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadConfig
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadSetting
import com.mozhimen.emulatork.input.virtual.sensor.SensorTilt
import com.swordfish.libretrodroid.GLRetroView
import com.swordfish.radialgamepad.library.RadialGamePad
import com.swordfish.radialgamepad.library.config.RadialGamePadTheme
import com.swordfish.radialgamepad.library.event.Event
import com.swordfish.radialgamepad.library.event.GestureType
import com.swordfish.radialgamepad.library.haptics.HapticConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import com.mozhimen.emulatork.input.theme.InputTheme
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadSettingManager
import com.mozhimen.emulatork.input.virtual.menu.MenuContract
import com.mozhimen.emulatork.input.virtual.gamepad.EGamepadOrientation
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadCustomizer

/**
 * @ClassName GameActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
abstract class AbsGameActivity : AbsGameActivity() {
    //    @Inject
//    lateinit var sharedPreferences: Lazy<SharedPreferences>
    abstract fun sharedPreferences(): Lazy<SharedPreferences>

    abstract fun gameServiceClass(): Class<out AbsGameService>

    private lateinit var horizontalDivider: View
    private lateinit var leftVerticalDivider: View
    private lateinit var rightVerticalDivider: View

    private var serviceController: AbsGameService.GameServiceController? = null

    private lateinit var sensorTilt: SensorTilt
    private var currentGyro: Gyro? = null

    private var leftPad: RadialGamePad? = null
    private var rightPad: RadialGamePad? = null

    private val touchControllerJobs = mutableSetOf<Job>()

    private val flowGamepadSetting = MutableStateFlow<GamepadSetting?>(null)
    private val insetsState = MutableStateFlow<Rect?>(null)
    private val orientationState = MutableStateFlow(Configuration.ORIENTATION_PORTRAIT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orientationState.value = getCurrentOrientation()

        sensorTilt = SensorTilt(applicationContext)

        horizontalDivider = findViewById(R.id.horizontaldividier)
        leftVerticalDivider = findViewById(R.id.leftverticaldivider)
        rightVerticalDivider = findViewById(R.id.rightverticaldivider)

        initializeInsetsState()

        startGameService()

        initializeFlows()
    }

    private fun initializeFlows() {
        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeTouchControllerFlow()
        }

        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeTiltSensitivityFlow()
        }

        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeTouchControllerVisibilityFlow()
        }

        runOnLifecycleState(Lifecycle.State.RESUMED) {
            initializeTiltEventsFlow()
        }
    }

    private suspend fun initializeTouchControllerVisibilityFlow() {
        isTouchControllerVisible()
            .collectSafe {
                leftGamePadContainer.isVisible = it
                rightGamePadContainer.isVisible = it
            }
    }

    private suspend fun initializeTiltEventsFlow() {
        sensorTilt
            .getTiltEvents()
            .collectSafe { sendTiltEvent(it) }
    }

    private suspend fun initializeTiltSensitivityFlow() {
        val sensitivity = settingManager().tiltSensitivity()
        sensorTilt.setSensitivity(sensitivity)
    }

    private fun initializeInsetsState() {
        mainContainerLayout.setOnApplyWindowInsetsListener { _, windowInsets ->
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.displayCutout()
                )
                Rect(insets.left, insets.top, insets.right, insets.bottom)
            } else {
                Rect(0, 0, 0, 0)
            }
            insetsState.value = result
            windowInsets
        }
    }

    private suspend fun initializeTouchControllerFlow() {
        val touchControllerFeatures = combine(getTouchControllerType(), orientationState, ::NTuple2)
            .onEach { (pad, orientation) -> setupController(pad, orientation) }

        val layoutFeatures = combine(
            isTouchControllerVisible(),
            flowGamepadSetting.filterNotNull(),
            insetsState.filterNotNull(),
            ::NTuple3
        )

        touchControllerFeatures.combine(layoutFeatures) { e1, e2 -> e1 + e2 }
            .collectSafe { (config, orientation, touchControllerVisible, padSettings, insets) ->
                LayoutHandler().updateLayout(config, padSettings, orientation, touchControllerVisible, insets)
            }
    }

    private fun getTouchControllerType() = getControllerType()
        .map { it[0] }
        .filterNotNull()
        .distinctUntilChanged()

    private suspend fun setupController(gamepadConfig: GamepadConfig, orientation: Int) {
        val hapticFeedbackMode = settingManager().hapticFeedbackMode()
        withContext(Dispatchers.Main) {
            setupTouchViews(gamepadConfig, hapticFeedbackMode, orientation)
        }
        loadTouchControllerSettings(gamepadConfig, orientation)
    }

    private fun isTouchControllerVisible(): Flow<Boolean> {
        return inputUnitManager()
            .getEnabledInputsObservable()
            .map { it.isEmpty() }
    }

    private fun getCurrentOrientation() = resources.configuration.orientation

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientationState.value = newConfig.orientation
    }

    private fun setupTouchViews(
        gamepadConfig: GamepadConfig,
        hapticFeedbackType: String,
        orientation: Int
    ) {
        touchControllerJobs
            .forEach { it.cancel() }

        touchControllerJobs.clear()

        leftGamePadContainer.removeAllViews()
        rightGamePadContainer.removeAllViews()

        val inputAreaConfig = gamepadConfig.getInputAreaConfig()

        val hapticConfig = when (hapticFeedbackType) {
            "none" -> HapticConfig.OFF
            "press" -> HapticConfig.PRESS
            "press_release" -> HapticConfig.PRESS_AND_RELEASE
            else -> HapticConfig.OFF
        }

        val theme = InputTheme.getGamePadTheme(leftGamePadContainer)

        val leftConfig = InputConfigRadialGamePadProvider.getRadialGamePadConfig(
            inputAreaConfig.inputAreaLeft,
            hapticConfig,
            theme
        )
        val leftPad = RadialGamePad(leftConfig, DEFAULT_MARGINS_DP, this)
        leftGamePadContainer.addView(leftPad)

        val rightConfig = InputConfigRadialGamePadProvider.getRadialGamePadConfig(
            inputAreaConfig.inputAreaRight,
            hapticConfig,
            theme
        )
        val rightPad = RadialGamePad(rightConfig, DEFAULT_MARGINS_DP, this)
        rightGamePadContainer.addView(rightPad)

        val touchControllerEvents = merge(leftPad.events(), rightPad.events())
            .shareIn(lifecycleScope, SharingStarted.Lazily)

        setupDefaultActions(touchControllerEvents)
        setupTiltActions(touchControllerEvents)
        setupTouchMenuActions(touchControllerEvents)

        this.leftPad = leftPad
        this.rightPad = rightPad
    }

    private fun setupDefaultActions(touchControllerEvents: Flow<Event>) {
        val job = lifecycleScope.launch {
            touchControllerEvents
                .collectSafe {
                    when (it) {
                        is Event.Button -> {
                            handleGamePadButton(it)
                        }

                        is Event.Direction -> {
                            handleGamePadDirection(it)
                        }
                    }
                }
        }
        touchControllerJobs.add(job)
    }

    private fun setupTiltActions(touchControllerEvents: Flow<Event>) {
        val job1 = lifecycleScope.launch {
            touchControllerEvents
                .filterIsInstance<Event.Gesture>()
                .filter { it.type == GestureType.TRIPLE_TAP }
                .batch_ofTime(500)
                .filter { it.isNotEmpty() }
                .collectSafe { events ->
                    handleTripleTaps(events)
                }
        }

        val job2 = lifecycleScope.launch {
            touchControllerEvents
                .filterIsInstance<Event.Gesture>()
                .filter { it.type == GestureType.FIRST_TOUCH }
                .collectSafe { event ->
                    currentGyro?.let { tracker ->
                        if (event.id in tracker.trackedIds()) {
                            stopTrackingId(tracker)
                        }
                    }
                }
        }

        touchControllerJobs.add(job1)
        touchControllerJobs.add(job2)
    }

    private fun setupTouchMenuActions(touchControllerEvents: Flow<Event>) {
        VirtualLongPressHandler.initializeTheme(this)

        val allMenuButtonEvents = touchControllerEvents
            .filterIsInstance<Event.Button>()
            .filter { it.id == KeyEvent.KEYCODE_BUTTON_MODE }
            .shareIn(lifecycleScope, SharingStarted.Lazily)

        val cancelMenuButtonEvents = allMenuButtonEvents
            .filter { it.action == KeyEvent.ACTION_UP }
            .map { Unit }

        val job = lifecycleScope.launch {
            allMenuButtonEvents
                .filter { it.action == KeyEvent.ACTION_DOWN }
                .map {
                    VirtualLongPressHandler.displayLoading(
                        this@AbsGameActivity,
                        R.drawable.ic_menu,
                        cancelMenuButtonEvents
                    )
                }
                .filter { it }
                .collectSafe {
                    displayOptionsDialog()
                    simulateTouchControllerHaptic()
                }
        }

        touchControllerJobs.add(job)
    }

    private fun handleTripleTaps(events: List<Event.Gesture>) {
        val eventsTracker = when (events.map { it.id }.toSet()) {
            setOf(InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK) -> GyroStick(
                InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK
            )

            setOf(InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_STICK) -> GyroStick(
                InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_STICK
            )

            setOf(InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD) -> GyroCross(
                InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD
            )

            setOf(InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD_AND_LEFT_STICK) -> GyroCross(
                InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD_AND_LEFT_STICK
            )

            setOf(InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_DPAD) -> GyroCross(
                InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_DPAD
            )

            setOf(
                KeyEvent.KEYCODE_BUTTON_L1,
                KeyEvent.KEYCODE_BUTTON_R1
            ) -> GyroTwoButtons(
                KeyEvent.KEYCODE_BUTTON_L1,
                KeyEvent.KEYCODE_BUTTON_R1
            )

            setOf(
                KeyEvent.KEYCODE_BUTTON_L2,
                KeyEvent.KEYCODE_BUTTON_R2
            ) -> GyroTwoButtons(
                KeyEvent.KEYCODE_BUTTON_L2,
                KeyEvent.KEYCODE_BUTTON_R2
            )

            else -> null
        }

        eventsTracker?.let { startTrackingId(eventsTracker) }
    }

    override fun onDestroy() {
        stopGameService()
        touchControllerJobs.clear()
        super.onDestroy()
    }

    private fun startGameService() {
        serviceController = AbsGameService.startService(applicationContext, gameServiceClass(), game)
    }

    private fun stopGameService() {
        serviceController = AbsGameService.stopService(applicationContext, serviceController)
    }

    override fun onFinishTriggered() {
        super.onFinishTriggered()
        stopGameService()
    }

    private fun handleGamePadButton(it: Event.Button) {
        retroGameView?.sendKeyEvent(it.action, it.id)
    }

    private fun handleGamePadDirection(it: Event.Direction) {
        when (it.id) {
            InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD -> {
                retroGameView?.sendMotionEvent(GLRetroView.MOTION_SOURCE_DPAD, it.xAxis, it.yAxis)
            }

            InputConfigRadialGamePadProvider.MOTION_SOURCE_LEFT_STICK -> {
                retroGameView?.sendMotionEvent(
                    GLRetroView.MOTION_SOURCE_ANALOG_LEFT,
                    it.xAxis,
                    it.yAxis
                )
            }

            InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_STICK -> {
                retroGameView?.sendMotionEvent(
                    GLRetroView.MOTION_SOURCE_ANALOG_RIGHT,
                    it.xAxis,
                    it.yAxis
                )
            }

            InputConfigRadialGamePadProvider.MOTION_SOURCE_DPAD_AND_LEFT_STICK -> {
                retroGameView?.sendMotionEvent(
                    GLRetroView.MOTION_SOURCE_ANALOG_LEFT,
                    it.xAxis,
                    it.yAxis
                )
                retroGameView?.sendMotionEvent(GLRetroView.MOTION_SOURCE_DPAD, it.xAxis, it.yAxis)
            }

            InputConfigRadialGamePadProvider.MOTION_SOURCE_RIGHT_DPAD -> {
                retroGameView?.sendMotionEvent(
                    GLRetroView.MOTION_SOURCE_ANALOG_RIGHT,
                    it.xAxis,
                    it.yAxis
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DIALOG_REQUEST) {
            if (data?.getBooleanExtra(MenuContract.RESULT_EDIT_TOUCH_CONTROLS, false) == true) {
                lifecycleScope.launch {
                    displayCustomizationOptions()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorTilt.isAllowedToRun = false
    }

    override fun onResume() {
        super.onResume()
        sensorTilt.isAllowedToRun = true
    }

    private fun sendTiltEvent(sensorValues: FloatArray) {
        currentGyro?.let {
            val xTilt = (sensorValues[0] + 1f) / 2f
            val yTilt = (sensorValues[1] + 1f) / 2f
            it.updateTracking(xTilt, yTilt, sequenceOf(leftPad, rightPad).filterNotNull())
        }
    }

    private fun stopTrackingId(trackedEvent: Gyro) {
        currentGyro = null
        sensorTilt.shouldRun = false
        trackedEvent.stopTracking(sequenceOf(leftPad, rightPad).filterNotNull())
    }

    private fun startTrackingId(trackedEvent: Gyro) {
        if (currentGyro != trackedEvent) {
            currentGyro?.let { stopTrackingId(it) }
            currentGyro = trackedEvent
            sensorTilt.shouldRun = true
            simulateTouchControllerHaptic()
        }
    }

    private fun simulateTouchControllerHaptic() {
        leftPad?.performHapticFeedback()
    }

    private suspend fun storeTouchControllerSettings(
        gamepadConfig: GamepadConfig,
        orientation: Int,
        settings: GamepadSetting
    ) {
        val settingsManager = getTouchControllerSettingsManager(gamepadConfig, orientation)
        return settingsManager.storeSetting(settings)
    }

    private suspend fun loadTouchControllerSettings(
        gamepadConfig: GamepadConfig,
        orientation: Int
    ) {
        val settingsManager = getTouchControllerSettingsManager(gamepadConfig, orientation)
        flowGamepadSetting.value = settingsManager.retrieveSetting()
    }

    private fun getTouchControllerSettingsManager(
        gamepadConfig: GamepadConfig,
        orientation: Int
    ): GamepadSettingManager {
        val settingsOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            EGamepadOrientation.PORTRAIT
        } else {
            EGamepadOrientation.LANDSCAPE
        }

        return GamepadSettingManager(
            applicationContext,
            gamepadConfig.inputType,
            sharedPreferences(),
            settingsOrientation
        )
    }

    private suspend fun displayCustomizationOptions() {
        findViewById<View>(R.id.editcontrolsdarkening).isVisible = true

        val customizer = GamepadCustomizer()

        val insets = insetsState
            .filterNotNull()
            .first()

        val touchControllerConfig = getTouchControllerType()
            .first()

        val padSettings = flowGamepadSetting.filterNotNull()
            .first()

        val initialSettings = GamepadSetting(
            padSettings.scale,
            padSettings.rotation,
            padSettings.marginX,
            padSettings.marginY
        )

        val finalSettings = customizer.displayCustomizationPopup(
            this@AbsGameActivity,
            layoutInflater,
            mainContainerLayout,
            insets,
            initialSettings
        )
            .takeWhile { it !is GamepadCustomizer.Event.Close }
            .scan(padSettings) { current, it ->
                when (it) {
                    is GamepadCustomizer.Event.Scale -> {
                        current.copy(scale = it.value)
                    }

                    is GamepadCustomizer.Event.Rotation -> {
                        current.copy(rotation = it.value)
                    }

                    is GamepadCustomizer.Event.Margins -> {
                        current.copy(marginX = it.x, marginY = it.y)
                    }

                    else -> current
                }
            }
            .onEach { flowGamepadSetting.value = it }
            .last()

        storeTouchControllerSettings(touchControllerConfig, orientationState.value, finalSettings)
        findViewById<View>(R.id.editcontrolsdarkening).isVisible = false
    }

    inner class LayoutHandler {

        private fun handleRetroViewLayout(
            constraintSet: ConstraintSet,
            gamepadConfig: GamepadConfig,
            orientation: Int,
            touchControllerVisible: Boolean,
            insets: Rect
        ) {
            if (!touchControllerVisible) {
                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT
                )
                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.RIGHT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT
                )
                return
            }

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.BOTTOM,
                    R.id.horizontaldividier,
                    ConstraintSet.TOP
                )

                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT
                )

                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.RIGHT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT
                )

                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
            } else {
                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )

                constraintSet.connect(
                    R.id.gamecontainer,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )

                if (gamepadConfig.allowTouchOverlay) {
                    constraintSet.connect(
                        R.id.gamecontainer,
                        ConstraintSet.LEFT,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.LEFT
                    )

                    constraintSet.connect(
                        R.id.gamecontainer,
                        ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.RIGHT
                    )
                } else {
                    constraintSet.connect(
                        R.id.gamecontainer,
                        ConstraintSet.LEFT,
                        R.id.leftverticaldivider,
                        ConstraintSet.RIGHT
                    )

                    constraintSet.connect(
                        R.id.gamecontainer,
                        ConstraintSet.RIGHT,
                        R.id.rightverticaldivider,
                        ConstraintSet.LEFT
                    )
                }
            }

            constraintSet.constrainedWidth(R.id.gamecontainer, true)
            constraintSet.constrainedHeight(R.id.gamecontainer, true)

            constraintSet.setMargin(R.id.gamecontainer, ConstraintSet.TOP, insets.top)
        }

        private fun handleTouchControllerLayout(
            constraintSet: ConstraintSet,
            gamepadSetting: GamepadSetting,
            gamepadConfig: GamepadConfig,
            orientation: Int,
            insets: Rect
        ) {
            val touchControllerConfig = gamepadConfig.getInputAreaConfig()

            val leftPad = leftPad ?: return
            val rightPad = rightPad ?: return

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                constraintSet.clear(R.id.leftgamepad, ConstraintSet.TOP)
                constraintSet.clear(R.id.rightgamepad, ConstraintSet.TOP)
            } else {
                constraintSet.connect(
                    R.id.leftgamepad,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    R.id.rightgamepad,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
            }

            val minScale = GamepadSetting.MIN_SCALE
            val maxScale = GamepadSetting.MAX_SCALE

            val leftScale = UtilKMathInterpolation.get_ofLinear(
                gamepadSetting.scale,
                minScale,
                maxScale
            ) * touchControllerConfig.scaleLeft

            val rightScale = UtilKMathInterpolation.get_ofLinear(
                gamepadSetting.scale,
                minScale,
                maxScale
            ) * touchControllerConfig.scaleRight

            val maxMargins = GamepadSetting.MAX_MARGINS.dp2px()

            constraintSet.setHorizontalWeight(R.id.leftgamepad, touchControllerConfig.scaleLeft)
            constraintSet.setHorizontalWeight(R.id.rightgamepad, touchControllerConfig.scaleRight)

            leftPad.primaryDialMaxSizeDp = DEFAULT_PRIMARY_DIAL_SIZE * leftScale
            rightPad.primaryDialMaxSizeDp = DEFAULT_PRIMARY_DIAL_SIZE * rightScale

            val baseVerticalMargin = touchControllerConfig.verticalMarginDP.dp2px()

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                setupMarginsForPortrait(
                    leftPad,
                    rightPad,
                    maxMargins,
                    gamepadSetting,
                    baseVerticalMargin.roundToInt() + insets.bottom
                )
            } else {
                setupMarginsForLandscape(
                    leftPad,
                    rightPad,
                    maxMargins,
                    gamepadSetting,
                    baseVerticalMargin.roundToInt() + insets.bottom,
                    maxOf(insets.left, insets.right)
                )
            }

            leftPad.gravityY = 1f
            rightPad.gravityY = 1f

            leftPad.gravityX = -1f
            rightPad.gravityX = 1f

            leftPad.secondaryDialSpacing = 0.1f
            rightPad.secondaryDialSpacing = 0.1f

            val constrainHeight = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                ConstraintSet.WRAP_CONTENT
            } else {
                ConstraintSet.MATCH_CONSTRAINT
            }

            val constrainWidth = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                ConstraintSet.MATCH_CONSTRAINT
            } else {
                ConstraintSet.WRAP_CONTENT
            }

            constraintSet.constrainHeight(R.id.leftgamepad, constrainHeight)
            constraintSet.constrainHeight(R.id.rightgamepad, constrainHeight)
            constraintSet.constrainWidth(R.id.leftgamepad, constrainWidth)
            constraintSet.constrainWidth(R.id.rightgamepad, constrainWidth)

            if (gamepadConfig.allowTouchRotation) {
                val maxRotation = GamepadSetting.MAX_ROTATION
                leftPad.secondaryDialRotation = UtilKMathInterpolation.get_ofLinear(gamepadSetting.rotation, 0f, maxRotation)
                rightPad.secondaryDialRotation = -UtilKMathInterpolation.get_ofLinear(gamepadSetting.rotation, 0f, maxRotation)
            }
        }

        private fun setupMarginsForLandscape(
            leftPad: RadialGamePad,
            rightPad: RadialGamePad,
            maxMargins: Float,
            gamepadSetting: GamepadSetting,
            verticalSpacing: Int,
            horizontalSpacing: Int
        ) {
            leftPad.spacingBottom = verticalSpacing
            leftPad.spacingLeft = UtilKMathInterpolation.get_ofLinear(
                gamepadSetting.marginX,
                0f,
                maxMargins
            ).roundToInt() + horizontalSpacing

            rightPad.spacingBottom = verticalSpacing
            rightPad.spacingRight = UtilKMathInterpolation.get_ofLinear(
                gamepadSetting.marginX,
                0f,
                maxMargins
            ).roundToInt() + horizontalSpacing

            leftPad.offsetX = 0f
            rightPad.offsetX = 0f

            leftPad.offsetY = -UtilKMathInterpolation.get_ofLinear(gamepadSetting.marginY, 0f, maxMargins)
            rightPad.offsetY = -UtilKMathInterpolation.get_ofLinear(gamepadSetting.marginY, 0f, maxMargins)
        }

        private fun setupMarginsForPortrait(
            leftPad: RadialGamePad,
            rightPad: RadialGamePad,
            maxMargins: Float,
            gamepadSetting: GamepadSetting,
            verticalSpacing: Int
        ) {
            leftPad.spacingBottom = UtilKMathInterpolation.get_ofLinear(
                gamepadSetting.marginY,
                0f,
                maxMargins
            ).roundToInt() + verticalSpacing
            leftPad.spacingLeft = 0
            rightPad.spacingBottom = UtilKMathInterpolation.get_ofLinear(
                gamepadSetting.marginY,
                0f,
                maxMargins
            ).roundToInt() + verticalSpacing
            rightPad.spacingRight = 0

            leftPad.offsetX = UtilKMathInterpolation.get_ofLinear(gamepadSetting.marginX, 0f, maxMargins)
            rightPad.offsetX = -UtilKMathInterpolation.get_ofLinear(gamepadSetting.marginX, 0f, maxMargins)

            leftPad.offsetY = 0f
            rightPad.offsetY = 0f
        }

        private fun updateDividers(
            orientation: Int,
            gamepadConfig: GamepadConfig,
            touchControllerVisible: Boolean
        ) {
            val theme = InputTheme.getGamePadTheme(leftGamePadContainer)

            val displayHorizontalDivider = UtilKBoolean.allTrue(
                orientation == Configuration.ORIENTATION_PORTRAIT,
                touchControllerVisible
            )

            val displayVerticalDivider = UtilKBoolean.allTrue(
                orientation != Configuration.ORIENTATION_PORTRAIT,
                !gamepadConfig.allowTouchOverlay,
                touchControllerVisible
            )

            updateDivider(horizontalDivider, displayHorizontalDivider, theme)
            updateDivider(leftVerticalDivider, displayVerticalDivider, theme)
            updateDivider(rightVerticalDivider, displayVerticalDivider, theme)
        }

        private fun updateDivider(divider: View, visible: Boolean, theme: RadialGamePadTheme) {
            divider.isVisible = visible
            divider.setBackgroundColor(theme.backgroundStrokeColor)
        }

        fun updateLayout(
            gamepadConfig: GamepadConfig,
            gamepadSetting: GamepadSetting,
            orientation: Int,
            touchControllerVisible: Boolean,
            insets: Rect
        ) {
            updateDividers(orientation, gamepadConfig, touchControllerVisible)

            val constraintSet = ConstraintSet()
            constraintSet.clone(mainContainerLayout)

            handleTouchControllerLayout(constraintSet, gamepadSetting, gamepadConfig, orientation, insets)
            handleRetroViewLayout(constraintSet, gamepadConfig, orientation, touchControllerVisible, insets)

            constraintSet.applyTo(mainContainerLayout)

            mainContainerLayout.requestLayout()
            mainContainerLayout.invalidate()
        }
    }

    companion object {
        const val DEFAULT_MARGINS_DP = 8f
        const val DEFAULT_PRIMARY_DIAL_SIZE = 160f

        fun launchGame(
            activity: Activity,
            gameActivityClazz: Class<*>,
            coreBundle: CoreBundle,
            game: Game,
            loadSave: Boolean,
            useLeanback: Boolean
        ) {
            val gameActivity = /*if (useLeanback) {
                TVGameActivity::class.java
            } else {*/
                gameActivityClazz
//            }
            activity.startActivityForResult(
                Intent(activity, gameActivity).apply {
                    putExtra(EXTRA_GAME, game)
                    putExtra(EXTRA_LOAD_SAVE, loadSave)
                    putExtra(EXTRA_LEANBACK, useLeanback)
                    putExtra(EXTRA_SYSTEM_CORE_CONFIG, coreBundle)
                },
                REQUEST_PLAY_GAME
            )
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}