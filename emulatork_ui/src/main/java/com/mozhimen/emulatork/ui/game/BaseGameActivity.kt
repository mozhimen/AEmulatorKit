package com.mozhimen.emulatork.ui.game

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.mozhimen.basick.utilk.androidx.lifecycle.runOnLifecycleState
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.kotlinx.coroutines.collectSafe
import com.mozhimen.basick.elemk.kotlinx.coroutines.VarPropertyMutableState
import com.mozhimen.basick.utilk.android.opengl.takeScreenshotOnMain
import com.mozhimen.basick.utilk.android.util.dp2pxI
import com.mozhimen.basick.utilk.kotlin.collections.zipOnKeys
import com.mozhimen.emulatork.basic.controller.ControllerConfig
import com.mozhimen.emulatork.basic.core.CoreVariable
import com.mozhimen.emulatork.basic.game.GameLoader
import com.mozhimen.emulatork.basic.game.GameLoaderError
import com.mozhimen.emulatork.basic.game.GameLoaderException
import com.mozhimen.emulatork.basic.library.ExposedSetting
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.SystemCoreConfig
import com.mozhimen.emulatork.basic.library.db.entities.Game
import com.mozhimen.emulatork.basic.saves.IncompatibleStateException
import com.mozhimen.emulatork.basic.saves.SaveState
import com.mozhimen.emulatork.basic.saves.SavesManager
import com.mozhimen.emulatork.basic.saves.StatesManager
import com.mozhimen.emulatork.basic.saves.StatesPreviewManager
import com.mozhimen.emulatork.basic.storage.RomFiles
import com.mozhimen.emulatork.ui.BuildConfig
import com.mozhimen.emulatork.ui.input.inputclass.getInputClass
import com.mozhimen.basick.elemk.mos.NTuple2
import com.mozhimen.basick.elemk.mos.NTuple4
import com.mozhimen.basick.utilk.android.content.get_of_config_longAnimTime
import com.mozhimen.basick.utilk.android.content.get_of_config_shortAnimTime
import com.mozhimen.basick.utilk.android.os.getStrDump
import com.mozhimen.basick.utilk.android.widget.showToast
import com.mozhimen.basick.utilk.kotlin.collections.filterNotNullValues
import com.mozhimen.basick.utilk.kotlin.array2indexedMap
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.main.ShaderChooser
import com.mozhimen.emulatork.ui.dagger.ImmersiveActivity
import com.mozhimen.emulatork.ui.dagger.feature.settings.SettingsManager
import com.mozhimen.emulatork.ui.coreoptions.CoreOption
import com.mozhimen.emulatork.ui.coreoptions.LemuroidCoreOption
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import com.mozhimen.emulatork.ui.input.InputKey
import com.mozhimen.emulatork.ui.rumble.RumbleManager
import com.mozhimen.emulatork.ui.settings.ControllerConfigsManager
import com.swordfish.libretrodroid.Controller
import com.swordfish.libretrodroid.GLRetroView
import com.swordfish.libretrodroid.GLRetroViewData
import com.swordfish.libretrodroid.Variable
import com.swordfish.libretrodroid.VirtualFile
import com.swordfish.radialgamepad.library.math.MathUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs
import kotlin.system.exitProcess

/**
 * @ClassName BaseGameActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@OptIn(FlowPreview::class, DelicateCoroutinesApi::class)
abstract class BaseGameActivity : ImmersiveActivity(), IUtilK {

    protected lateinit var game: Game
    private lateinit var system: GameSystem
    protected lateinit var systemCoreConfig: SystemCoreConfig
    protected lateinit var mainContainerLayout: ConstraintLayout
    private lateinit var gameContainerLayout: FrameLayout
    protected lateinit var leftGamePadContainer: FrameLayout
    protected lateinit var rightGamePadContainer: FrameLayout
    private lateinit var loadingView: ProgressBar
    private lateinit var loadingMessageView: TextView

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var statesManager: StatesManager

    @Inject
    lateinit var statesPreviewManager: StatesPreviewManager

    @Inject
    lateinit var legacySavesManager: SavesManager

    @Inject
    lateinit var coreVariablesManager: CoreVariablesManager

    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    @Inject
    lateinit var gameLoader: GameLoader

    @Inject
    lateinit var controllerConfigsManager: ControllerConfigsManager

    @Inject
    lateinit var rumbleManager: RumbleManager

    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    private val startGameTime = System.currentTimeMillis()

    private val keyEventsFlow: MutableSharedFlow<KeyEvent?> = MutableSharedFlow()
    private val motionEventsFlow: MutableSharedFlow<MotionEvent> = MutableSharedFlow()

    protected val retroGameViewFlow = MutableStateFlow<GLRetroView?>(null)
    protected var retroGameView: GLRetroView? by VarPropertyMutableState(retroGameViewFlow)

    private val loadingState = MutableStateFlow(false)
    private val loadingMessageStateFlow = MutableStateFlow("")
    private val controllerConfigsState = MutableStateFlow<Map<Int, ControllerConfig>>(mapOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        setUpExceptionsHandler()

        mainContainerLayout = findViewById(R.id.maincontainer)
        gameContainerLayout = findViewById(R.id.gamecontainer)
        loadingView = findViewById(R.id.progress)
        loadingMessageView = findViewById(R.id.progress_message)
        leftGamePadContainer = findViewById(R.id.leftgamepad)
        rightGamePadContainer = findViewById(R.id.rightgamepad)

        game = intent.getSerializableExtra(EXTRA_GAME) as Game
        systemCoreConfig = intent.getSerializableExtra(EXTRA_SYSTEM_CORE_CONFIG) as SystemCoreConfig
        system = GameSystem.findById(game.systemId)

        lifecycleScope.launch {
            loadGame()
        }

        initialiseFlows()
    }

    private fun initialiseFlows() {
        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeLoadingViewFlow()
        }

        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeControllerConfigsFlow()
        }

        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeGamePadShortcutsFlow()
        }

        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeGamePadKeysFlow()
        }

        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeVirtualGamePadMotionsFlow()
        }

        runOnLifecycleState(Lifecycle.State.STARTED) {
            initializeRetroGameViewErrorsFlow()
        }

        runOnLifecycleState(Lifecycle.State.CREATED) {
            initializeGamePadMotionsFlow()
        }

        runOnLifecycleState(Lifecycle.State.RESUMED) {
            initializeLoadingMessageFlow()
        }

        runOnLifecycleState(Lifecycle.State.RESUMED) {
            initializeLoadingVisibilityFlow()
        }

        runOnLifecycleState(Lifecycle.State.RESUMED) {
            initializeRumbleFlow()
        }

        runOnLifecycleState(Lifecycle.State.RESUMED) {
            initializeCoreVariablesFlow()
        }

        runOnLifecycleState(Lifecycle.State.RESUMED) {
            initializeControllersConfigFlow()
        }
    }

    private suspend fun initializeControllersConfigFlow() {
        try {
            waitRetroGameViewInitialized()
            val controllers = controllerConfigsManager.getControllerConfigs(system.id, systemCoreConfig)
            controllerConfigsState.value = controllers
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private suspend fun initializeRetroGameViewErrorsFlow() {
        retroGameViewFlow().getGLRetroErrors()
            .catch { Timber.e(it, "Exception in GLRetroErrors. Ironic.") }
            .collect { handleRetroViewError(it) }
    }

    private suspend fun initializeCoreVariablesFlow() {
        try {
            waitRetroGameViewInitialized()
            val options = coreVariablesManager.getOptionsForCore(system.id, systemCoreConfig)
            updateCoreVariables(options)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private suspend fun initializeLoadingVisibilityFlow() {
        loadingState
            .debounce(get_of_config_longAnimTime().toLong())
            .collectSafe {
                loadingView.isVisible = it
                loadingMessageView.isVisible = it
            }
    }

    private suspend fun initializeLoadingMessageFlow() {
        loadingMessageStateFlow
            .debounce(2 * get_of_config_longAnimTime().toLong())
            .collectSafe {
                loadingMessageView.text = it
            }
    }

    private suspend fun initializeControllerConfigsFlow() {
        waitGLEvent<GLRetroView.GLRetroEvents.FrameRendered>()
        controllerConfigsState.collectSafe {
            updateControllers(it)
        }
    }

    private suspend inline fun <reified T> waitGLEvent() {
        val retroView = retroGameViewFlow()
        retroView.getGLRetroEvents()
            .filterIsInstance<T>()
            .first()
    }

    private suspend fun waitRetroGameViewInitialized() {
        retroGameViewFlow()
    }

    private suspend fun initializeRumbleFlow() {
        val retroGameView = retroGameViewFlow()
        val rumbleEvents = retroGameView.getRumbleEvents()
        rumbleManager.collectAndProcessRumbleEvents(systemCoreConfig, rumbleEvents)
    }

    private fun setUpExceptionsHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            performUnexpectedErrorFinish(exception)
            defaultExceptionHandler?.uncaughtException(thread, exception)
        }
    }

    fun getControllerType(): Flow<Map<Int, ControllerConfig>> {
        return controllerConfigsState
    }

    /* On some cores unserialize fails with no reason. So we need to try multiple times. */
    private suspend fun restoreAutoSaveAsync(saveState: SaveState) {
        // PPSSPP and Mupen64 initialize some state while rendering the first frame, so we have to wait before restoring
        // the autosave. Do not change thread here. Stick to the GL one to avoid issues with PPSSPP.
        if (!isAutoSaveEnabled()) return

        try {
            waitGLEvent<GLRetroView.GLRetroEvents.FrameRendered>()
            restoreQuickSave(saveState)
        } catch (e: Throwable) {
            Timber.e(e, "Error while loading auto-save")
        }
    }

    private suspend fun takeScreenshotPreview(index: Int) {
        val sizeInDp = StatesPreviewManager.PREVIEW_SIZE_DP
        val previewSize = sizeInDp.dp2pxI()
        val preview = retroGameView?.takeScreenshotOnMain(previewSize, 3)
        if (preview != null) {
            statesPreviewManager.setPreviewForSlot(game, preview, systemCoreConfig.coreID, index)
        }
    }

    private fun initializeRetroGameView(
        gameData: GameLoader.GameData,
        hdMode: Boolean,
        forceLegacyHdMode: Boolean,
        screenFilter: String,
        lowLatencyAudio: Boolean,
        enableRumble: Boolean
    ): GLRetroView {
        val data = GLRetroViewData(this).apply {
            coreFilePath = gameData.coreLibrary

            when (val gameFiles = gameData.gameFiles) {
                is RomFiles.Standard -> {
                    gameFilePath = gameFiles.files.first().absolutePath
                }

                is RomFiles.Virtual -> {
                    gameVirtualFiles = gameFiles.files
                        .map { VirtualFile(it.filePath, it.fd) }
                }
            }

            systemDirectory = gameData.systemDirectory.absolutePath
            savesDirectory = gameData.savesDirectory.absolutePath
            variables = gameData.coreVariables.map { Variable(it.key, it.value) }.toTypedArray()
            saveRAMState = gameData.saveRAMData
            shader = ShaderChooser.getShaderForSystem(
                applicationContext,
                hdMode,
                forceLegacyHdMode,
                screenFilter,
                system
            )
            preferLowLatencyAudio = lowLatencyAudio
            rumbleEventsEnabled = enableRumble
            skipDuplicateFrames = systemCoreConfig.skipDuplicateFrames
        }

        val retroGameView = GLRetroView(this, data)
        retroGameView.isFocusable = false
        retroGameView.isFocusableInTouchMode = false

        lifecycle.addObserver(retroGameView)
        gameContainerLayout.addView(retroGameView)

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        retroGameView.layoutParams = layoutParams

        lifecycleScope.launch {
            gameData.quickSaveData?.let {
                restoreAutoSaveAsync(it)
            }
        }

        if (BuildConfig.DEBUG) {
            printRetroVariables(retroGameView)
        }

        return retroGameView
    }

    private fun printRetroVariables(retroGameView: GLRetroView) {
        retroGameView.getVariables().forEach {
            Timber.i("Libretro variable: $it")
        }
    }

    private fun updateControllers(controllers: Map<Int, ControllerConfig>) {
        retroGameView
            ?.getControllers()?.array2indexedMap()
            ?.zipOnKeys(controllers, this::findControllerId)
            ?.filterNotNullValues()
            ?.forEach { (port, controllerId) ->
                Timber.i("Controls setting $port to $controllerId")
                retroGameView?.setControllerType(port, controllerId)
            }
    }

    private fun findControllerId(
        supported: Array<Controller>,
        controllerConfig: ControllerConfig
    ): Int? {
        return supported
            .firstOrNull { controller ->
                sequenceOf(
                    controller.id == controllerConfig.libretroId,
                    controller.description == controllerConfig.libretroDescriptor
                ).any { it }
            }?.id
    }

    private fun handleRetroViewError(errorCode: Int) {
        Timber.e("Error in GLRetroView $errorCode")
        val gameLoaderError = when (errorCode) {
            GLRetroView.ERROR_GL_NOT_COMPATIBLE -> GameLoaderError.GLIncompatible
            GLRetroView.ERROR_LOAD_GAME -> GameLoaderError.LoadGame
            GLRetroView.ERROR_LOAD_LIBRARY -> GameLoaderError.LoadCore
            GLRetroView.ERROR_SERIALIZATION -> GameLoaderError.Saves
            else -> GameLoaderError.Generic
        }
        retroGameView = null
        displayGameLoaderError(gameLoaderError, systemCoreConfig)
    }

    private fun transformExposedSetting(
        exposedSetting: ExposedSetting,
        coreOptions: List<CoreOption>
    ): LemuroidCoreOption? {
        return coreOptions
            .firstOrNull { it.variable.key == exposedSetting.key }
            ?.let { LemuroidCoreOption(exposedSetting, it) }
    }

    protected fun displayOptionsDialog() {
        if (loadingState.value) return

        val coreOptions = getCoreOptions()

        val options = systemCoreConfig.exposedSettings
            .mapNotNull { transformExposedSetting(it, coreOptions) }

        val advancedOptions = systemCoreConfig.exposedAdvancedSettings
            .mapNotNull { transformExposedSetting(it, coreOptions) }

        val intent = Intent(this, getDialogClass()).apply {
            this.putExtra(GameMenuContract.EXTRA_CORE_OPTIONS, options.toTypedArray())
            this.putExtra(GameMenuContract.EXTRA_ADVANCED_CORE_OPTIONS, advancedOptions.toTypedArray())
            this.putExtra(GameMenuContract.EXTRA_CURRENT_DISK, retroGameView?.getCurrentDisk() ?: 0)
            this.putExtra(GameMenuContract.EXTRA_DISKS, retroGameView?.getAvailableDisks() ?: 0)
            this.putExtra(GameMenuContract.EXTRA_GAME, game)
            this.putExtra(GameMenuContract.EXTRA_SYSTEM_CORE_CONFIG, systemCoreConfig)
            this.putExtra(GameMenuContract.EXTRA_AUDIO_ENABLED, retroGameView?.audioEnabled)
            this.putExtra(GameMenuContract.EXTRA_FAST_FORWARD_SUPPORTED, system.fastForwardSupport)
            this.putExtra(GameMenuContract.EXTRA_FAST_FORWARD, (retroGameView?.frameSpeed ?: 1) > 1)
        }
        startActivityForResult(intent, DIALOG_REQUEST)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    protected abstract fun getDialogClass(): Class<out Activity>

    private suspend fun isAutoSaveEnabled(): Boolean {
        return systemCoreConfig.statesSupported && settingsManager.autoSave()
    }

    private fun getCoreOptions(): List<CoreOption> {
        return retroGameView?.getVariables()
            ?.map { CoreOption.fromLibretroDroidVariable(it) } ?: listOf()
    }

    private fun updateCoreVariables(options: List<CoreVariable>) {
        val updatedVariables = options.map { Variable(it.key, it.value) }
            .toTypedArray()

        updatedVariables.forEach {
            Timber.i("Updating core variable: ${it.key} ${it.value}")
        }

        retroGameView?.updateVariables(*updatedVariables)
    }

    // Now that we wait for the first rendered frame this is probably no longer needed, but we'll keep it just to be sure
    private suspend fun restoreQuickSave(saveState: SaveState) {
        var times = 10

        while (!loadSaveState(saveState) && times > 0) {
            delay(200)
            times--
        }
    }

    private suspend fun initializeGamePadShortcutsFlow() {
        inputDeviceManager.getInputMenuShortCutObservable()
            .distinctUntilChanged()
            .collectSafe { shortcut ->
                shortcut?.let {
                    resources.getString(R.string.game_toast_settings_button_using_gamepad, it.name).showToast()
                }
            }
    }

    data class SingleAxisEvent(val axis: Int, val action: Int, val keyCode: Int, val port: Int)

    private suspend fun initializeVirtualGamePadMotionsFlow() {
        val events = combine(
            inputDeviceManager.getGamePadsPortMapperObservable(),
            motionEventsFlow,
            ::NTuple2
        )

        events
            .mapNotNull { (ports, event) ->
                ports(event.device)?.let { it to event }
            }
            .map { (port, event) ->
                val axes = event.device.getInputClass().getAxesMap().entries

                axes.map { (axis, button) ->
                    val action = if (event.getAxisValue(axis) > 0.5) {
                        KeyEvent.ACTION_DOWN
                    } else {
                        KeyEvent.ACTION_UP
                    }
                    SingleAxisEvent(axis, action, button, port)
                }.toSet()
            }
            .scan(emptySet<SingleAxisEvent>()) { prev, next ->
                next.minus(prev).forEach {
                    retroGameView?.sendKeyEvent(it.action, it.keyCode, it.port)
                }
                next
            }
            .collectSafe { }
    }

    private suspend fun initializeGamePadMotionsFlow() {
        val events = combine(
            inputDeviceManager.getGamePadsPortMapperObservable(),
            motionEventsFlow,
            ::NTuple2
        )

        events
            .collectSafe { (ports, event) ->
                ports(event.device)?.let {
                    sendStickMotions(event, it)
                }
            }
    }

    private suspend fun initializeGamePadKeysFlow() {
        val pressedKeys = mutableSetOf<Int>()

        val filteredKeyEvents = keyEventsFlow
            .filterNotNull()
            .filter { it.repeatCount == 0 }
            .map { Triple(it.device, it.action, it.keyCode) }
            .distinctUntilChanged()

        val shortcutKeys = inputDeviceManager.getInputMenuShortCutObservable()
            .map { it?.keys ?: setOf() }

        val combinedObservable = combine(
            shortcutKeys,
            inputDeviceManager.getGamePadsPortMapperObservable(),
            inputDeviceManager.getInputBindingsObservable(),
            filteredKeyEvents,
            ::NTuple4
        )

        combinedObservable
            .onStart { pressedKeys.clear() }
            .onCompletion { pressedKeys.clear() }
            .collectSafe { (shortcut, ports, bindings, event) ->
                val (device, action, keyCode) = event
                val port = ports(device)
                val bindKeyCode = bindings(device)[InputKey(keyCode)]?.keyCode ?: keyCode

                if (bindKeyCode == KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_DOWN) {
                    onBackPressed()
                    return@collectSafe
                }

                if (port == 0) {
                    if (bindKeyCode == KeyEvent.KEYCODE_BUTTON_MODE && action == KeyEvent.ACTION_DOWN) {
                        displayOptionsDialog()
                        return@collectSafe
                    }

                    if (action == KeyEvent.ACTION_DOWN) {
                        pressedKeys.add(keyCode)
                    } else if (action == KeyEvent.ACTION_UP) {
                        pressedKeys.remove(keyCode)
                    }

                    if (shortcut.isNotEmpty() && pressedKeys.containsAll(shortcut)) {
                        displayOptionsDialog()
                        return@collectSafe
                    }
                }

                port?.let {
                    retroGameView?.sendKeyEvent(action, bindKeyCode, it)
                }
            }
    }

    private fun sendStickMotions(event: MotionEvent, port: Int) {
        if (port < 0) return
        when (event.source) {
            InputDevice.SOURCE_JOYSTICK -> {
                if (controllerConfigsState.value[port]?.mergeDPADAndLeftStickEvents == true) {
                    sendMergedMotionEvents(event, port)
                } else {
                    sendSeparateMotionEvents(event, port)
                }
            }
        }
    }

    private fun sendMergedMotionEvents(event: MotionEvent, port: Int) {
        val events = listOf(
            retrieveCoordinates(event, MotionEvent.AXIS_HAT_X, MotionEvent.AXIS_HAT_Y),
            retrieveCoordinates(event, MotionEvent.AXIS_X, MotionEvent.AXIS_Y)
        )

        val xVal = events.maxByOrNull { abs(it.x) }?.x ?: 0f
        val yVal = events.maxByOrNull { abs(it.y) }?.y ?: 0f

        retroGameView?.sendMotionEvent(GLRetroView.MOTION_SOURCE_DPAD, xVal, yVal, port)
        retroGameView?.sendMotionEvent(GLRetroView.MOTION_SOURCE_ANALOG_LEFT, xVal, yVal, port)

        sendStickMotion(
            event,
            GLRetroView.MOTION_SOURCE_ANALOG_RIGHT,
            MotionEvent.AXIS_Z,
            MotionEvent.AXIS_RZ,
            port
        )
    }

    private fun sendSeparateMotionEvents(event: MotionEvent, port: Int) {
        sendDPADMotion(
            event,
            GLRetroView.MOTION_SOURCE_DPAD,
            MotionEvent.AXIS_HAT_X,
            MotionEvent.AXIS_HAT_Y,
            port
        )
        sendStickMotion(
            event,
            GLRetroView.MOTION_SOURCE_ANALOG_LEFT,
            MotionEvent.AXIS_X,
            MotionEvent.AXIS_Y,
            port
        )
        sendStickMotion(
            event,
            GLRetroView.MOTION_SOURCE_ANALOG_RIGHT,
            MotionEvent.AXIS_Z,
            MotionEvent.AXIS_RZ,
            port
        )
    }

    private fun sendStickMotion(
        event: MotionEvent,
        source: Int,
        xAxis: Int,
        yAxis: Int,
        port: Int
    ) {
        val coords = retrieveCoordinates(event, xAxis, yAxis)
        retroGameView?.sendMotionEvent(source, coords.x, coords.y, port)
    }

    private fun sendDPADMotion(
        event: MotionEvent,
        source: Int,
        xAxis: Int,
        yAxis: Int,
        port: Int
    ) {
        retroGameView?.sendMotionEvent(source, event.getAxisValue(xAxis), event.getAxisValue(yAxis), port)
    }

    @Deprecated("This sadly creates some issues with certain controllers and input lag on very slow devices.")
    private fun retrieveNormalizedCoordinates(event: MotionEvent, xAxis: Int, yAxis: Int): PointF {
        val rawX = event.getAxisValue(xAxis)
        val rawY = -event.getAxisValue(yAxis)

        val angle = MathUtils.angle(0f, rawX, 0f, rawY)
        val distance = MathUtils.clamp(MathUtils.distance(0f, rawX, 0f, rawY), 0f, 1f)

        return MathUtils.convertPolarCoordinatesToSquares(angle, distance)
    }

    private fun retrieveCoordinates(event: MotionEvent, xAxis: Int, yAxis: Int): PointF {
        return PointF(event.getAxisValue(xAxis), event.getAxisValue(yAxis))
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            GlobalScope.launch {
                motionEventsFlow.emit(event)
            }
        }
        return super.onGenericMotionEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && InputKey(keyCode) in event.device.getInputClass().getInputKeys()) {
            lifecycleScope.launch {
                keyEventsFlow.emit(event)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && InputKey(keyCode) in event.device.getInputClass().getInputKeys()) {
            lifecycleScope.launch {
                keyEventsFlow.emit(event)
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (loadingState.value) return
        lifecycleScope.launch {
            autoSaveAndFinish()
        }
    }

    private suspend fun autoSaveAndFinish() = withLoading {
        saveSRAM(game)
        saveAutoSave(game)
        performSuccessfulActivityFinish()
    }

    private fun performSuccessfulActivityFinish() {
        val resultIntent = Intent().apply {
            putExtra(PLAY_GAME_RESULT_SESSION_DURATION, System.currentTimeMillis() - startGameTime)
            putExtra(PLAY_GAME_RESULT_GAME, intent.getSerializableExtra(EXTRA_GAME))
            putExtra(PLAY_GAME_RESULT_LEANBACK, intent.getBooleanExtra(EXTRA_LEANBACK, false))
        }

        setResult(Activity.RESULT_OK, resultIntent)

        finishAndExitProcess()
    }

    private inline fun withLoading(block: () -> Unit) {
        loadingState.value = true
        block()
        loadingState.value = false
    }

    private fun performUnexpectedErrorFinish(exception: Throwable) {
        Timber.e(exception, "Handling java exception in BaseGameActivity")
        val resultIntent = Intent().apply {
            putExtra(PLAY_GAME_RESULT_ERROR, exception.message)
        }

        setResult(RESULT_UNEXPECTED_ERROR, resultIntent)
        finishAndExitProcess()
    }

    private fun performErrorFinish(message: String) {
        val resultIntent = Intent().apply {
            putExtra(PLAY_GAME_RESULT_ERROR, message)
        }

        setResult(RESULT_ERROR, resultIntent)
        finishAndExitProcess()
    }

    private fun finishAndExitProcess() {
        onFinishTriggered()
        GlobalScope.launch {
            delay(get_of_config_shortAnimTime().toLong())
            exitProcess(0)
        }
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    open fun onFinishTriggered() {}

    private suspend fun saveAutoSave(game: Game) {
        if (!isAutoSaveEnabled()) return
        val state = getCurrentSaveState()

        if (state != null) {
            statesManager.setAutoSave(game, systemCoreConfig.coreID, state)
            Timber.i("Stored autosave file with size: ${state?.state?.size}")
        }
    }

    private suspend fun saveSRAM(game: Game) {
        val retroGameView = retroGameView ?: return
        val sramState = retroGameView.serializeSRAM()
        legacySavesManager.setSaveRAM(game, sramState)
        Timber.i("Stored sram file with size: ${sramState.size}")
    }

    private suspend fun saveSlot(index: Int) {
        if (loadingState.value) return
        withLoading {
            getCurrentSaveState()?.let {
                statesManager.setSlotSave(game, it, systemCoreConfig.coreID, index)
                runCatching {
                    takeScreenshotPreview(index)
                }
            }
        }
    }

    private suspend fun loadSlot(index: Int) {
        if (loadingState.value) return
        withLoading {
            try {
                statesManager.getSlotSave(game, systemCoreConfig.coreID, index)?.let {
                    val loaded = withContext(Dispatchers.IO) {
                        loadSaveState(it)
                    }
                    withContext(Dispatchers.Main) {
                        if (!loaded)
                            R.string.game_toast_load_state_failed.showToast()
                    }
                }
            } catch (e: Throwable) {
                displayLoadStateErrorMessage(e)
            }
        }
    }

    private fun getCurrentSaveState(): SaveState? {
        val retroGameView = retroGameView ?: return null
        val currentDisk = if (system.hasMultiDiskSupport) {
            retroGameView.getCurrentDisk()
        } else {
            0
        }
        return SaveState(
            retroGameView.serializeState(),
            SaveState.Metadata(currentDisk, systemCoreConfig.statesVersion)
        )
    }

    private fun loadSaveState(saveState: SaveState): Boolean {
        val retroGameView = retroGameView ?: return false

        if (systemCoreConfig.statesVersion != saveState.metadata.version) {
            throw IncompatibleStateException()
        }

        if (system.hasMultiDiskSupport &&
            retroGameView.getAvailableDisks() > 1 &&
            retroGameView.getCurrentDisk() != saveState.metadata.diskIndex
        ) {
            retroGameView.changeDisk(saveState.metadata.diskIndex)
        }

        return retroGameView.unserializeState(saveState.state)
    }

    private suspend fun displayLoadStateErrorMessage(throwable: Throwable) = withContext(Dispatchers.Main) {
        when (throwable) {
            is IncompatibleStateException ->
                R.string.error_message_incompatible_state.showToast(Toast.LENGTH_LONG)

            else -> R.string.game_toast_load_state_failed.showToast()
        }
    }

    private suspend fun reset() = withLoading {
        try {
            delay(get_of_config_longAnimTime().toLong())
            retroGameViewFlow().reset()
        } catch (e: Throwable) {
            Timber.e(e, "Error in reset")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DIALOG_REQUEST) {
            Timber.i("Game menu dialog response: ${data?.extras.getStrDump()}")
            if (data?.getBooleanExtra(GameMenuContract.RESULT_RESET, false) == true) {
                GlobalScope.launch {
                    reset()
                }
            }
            if (data?.hasExtra(GameMenuContract.RESULT_SAVE) == true) {
                GlobalScope.launch {
                    saveSlot(data.getIntExtra(GameMenuContract.RESULT_SAVE, 0))
                }
            }
            if (data?.hasExtra(GameMenuContract.RESULT_LOAD) == true) {
                GlobalScope.launch {
                    loadSlot(data.getIntExtra(GameMenuContract.RESULT_LOAD, 0))
                }
            }
            if (data?.getBooleanExtra(GameMenuContract.RESULT_QUIT, false) == true) {
                GlobalScope.launch {
                    autoSaveAndFinish()
                }
            }
            if (data?.hasExtra(GameMenuContract.RESULT_CHANGE_DISK) == true) {
                val index = data.getIntExtra(GameMenuContract.RESULT_CHANGE_DISK, 0)
                retroGameView?.changeDisk(index)
            }
            if (data?.hasExtra(GameMenuContract.RESULT_ENABLE_AUDIO) == true) {
                retroGameView?.apply {
                    this.audioEnabled = data.getBooleanExtra(
                        GameMenuContract.RESULT_ENABLE_AUDIO,
                        true
                    )
                }
            }
            if (data?.hasExtra(GameMenuContract.RESULT_ENABLE_FAST_FORWARD) == true) {
                retroGameView?.apply {
                    val fastForwardEnabled = data.getBooleanExtra(
                        GameMenuContract.RESULT_ENABLE_FAST_FORWARD,
                        false
                    )
                    this.frameSpeed = if (fastForwardEnabled) 2 else 1
                }
            }
        }
    }

    private suspend fun loadGame() {
        val requestLoadSave = intent.getBooleanExtra(EXTRA_LOAD_SAVE, false)

        val autoSaveEnabled = settingsManager.autoSave()
        val filter = settingsManager.screenFilter()
        val hdMode = settingsManager.hdMode()
        val forceLegacyHdMode = settingsManager.forceLegacyHdMode()
        val lowLatencyAudio = settingsManager.lowLatencyAudio()
        val enableRumble = settingsManager.enableRumble()
        val directLoad = settingsManager.allowDirectGameLoad()

        val loadingStatesFlow = gameLoader.load(
            applicationContext,
            game,
            requestLoadSave && autoSaveEnabled,
            systemCoreConfig,
            directLoad
        )

        loadingStatesFlow
            .flowOn(Dispatchers.IO)
            .catch {
                displayGameLoaderError((it as GameLoaderException).error, systemCoreConfig)
            }
            .collect { loadingState ->
                displayLoadingState(loadingState)
                if (loadingState is GameLoader.LoadingState.Ready) {
                    retroGameView = initializeRetroGameView(
                        loadingState.gameData,
                        hdMode,
                        forceLegacyHdMode,
                        filter,
                        lowLatencyAudio,
                        systemCoreConfig.rumbleSupported && enableRumble
                    )
                }
            }
    }

    private suspend fun initializeLoadingViewFlow() {
        withLoading {
            waitGLEvent<GLRetroView.GLRetroEvents.FrameRendered>()
        }
    }

    private suspend fun retroGameViewFlow() = retroGameViewFlow.filterNotNull().first()

    private fun displayLoadingState(loadingState: GameLoader.LoadingState) {
        loadingMessageStateFlow.value = when (loadingState) {
            is GameLoader.LoadingState.LoadingCore -> getString(R.string.game_loading_download_core)
            is GameLoader.LoadingState.LoadingGame -> getString(R.string.game_loading_preparing_game)
            else -> ""
        }
    }

    private fun displayGameLoaderError(gameError: GameLoaderError, coreConfig: SystemCoreConfig) {

        val messageId = when (gameError) {
            is GameLoaderError.GLIncompatible -> getString(R.string.game_loader_error_gl_incompatible)
            is GameLoaderError.Generic -> getString(R.string.game_loader_error_generic)
            is GameLoaderError.LoadCore -> getString(R.string.game_loader_error_load_core)
            is GameLoaderError.LoadGame -> getString(R.string.game_loader_error_load_game)
            is GameLoaderError.Saves -> getString(R.string.game_loader_error_save)
            is GameLoaderError.UnsupportedArchitecture -> getString(R.string.game_loader_error_unsupported_architecture)
            is GameLoaderError.MissingBiosFiles -> getString(
                R.string.game_loader_error_missing_bios,
                gameError.missingFiles
            )
        }

        performErrorFinish(messageId)
    }

    companion object {
        const val DIALOG_REQUEST = 100

        private const val EXTRA_GAME = "GAME"
        private const val EXTRA_LOAD_SAVE = "LOAD_SAVE"
        private const val EXTRA_LEANBACK = "LEANBACK"
        private const val EXTRA_SYSTEM_CORE_CONFIG = "EXTRA_SYSTEM_CORE_CONFIG"

        const val REQUEST_PLAY_GAME = 1001
        const val PLAY_GAME_RESULT_SESSION_DURATION = "PLAY_GAME_RESULT_SESSION_DURATION"
        const val PLAY_GAME_RESULT_GAME = "PLAY_GAME_RESULT_GAME"
        const val PLAY_GAME_RESULT_LEANBACK = "PLAY_GAME_RESULT_LEANBACK"
        const val PLAY_GAME_RESULT_ERROR = "PLAY_GAME_RESULT_ERROR"

        const val RESULT_ERROR = Activity.RESULT_FIRST_USER + 2
        const val RESULT_UNEXPECTED_ERROR = Activity.RESULT_FIRST_USER + 3

        fun launchGame(
            activity: Activity,
            systemCoreConfig: SystemCoreConfig,
            game: Game,
            loadSave: Boolean,
            useLeanback: Boolean
        ) {
            val gameActivity = /*if (useLeanback) {
                TVGameActivity::class.java
            } else {*/
                GameActivity::class.java
//            }
            activity.startActivityForResult(
                Intent(activity, gameActivity).apply {
                    putExtra(EXTRA_GAME, game)
                    putExtra(EXTRA_LOAD_SAVE, loadSave)
                    putExtra(EXTRA_LEANBACK, useLeanback)
                    putExtra(EXTRA_SYSTEM_CORE_CONFIG, systemCoreConfig)
                },
                REQUEST_PLAY_GAME
            )
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
