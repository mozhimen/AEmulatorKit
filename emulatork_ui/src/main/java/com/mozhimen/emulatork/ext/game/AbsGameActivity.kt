package com.mozhimen.emulatork.ext.game

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
import com.mozhimen.basick.BuildConfig
import com.mozhimen.basick.utilk.androidx.lifecycle.runOnLifecycleState
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.kotlinx.coroutines.collectSafe
import com.mozhimen.basick.elemk.kotlinx.coroutines.VarPropertyMutableState
import com.mozhimen.basick.utilk.android.opengl.takeScreenshotOnMain
import com.mozhimen.basick.utilk.android.util.dp2pxI
import com.mozhimen.basick.utilk.kotlin.collections.zipOnKeys
import com.mozhimen.basick.elemk.mos.NTuple2
import com.mozhimen.basick.elemk.mos.NTuple4
import com.mozhimen.basick.utilk.android.content.get_of_config_longAnimTime
import com.mozhimen.basick.utilk.android.content.get_of_config_shortAnimTime
import com.mozhimen.basick.utilk.android.os.getStrDump
import com.mozhimen.basick.utilk.android.widget.showToast
import com.mozhimen.basick.utilk.kotlin.collections.filterNotNullValues
import com.mozhimen.basick.utilk.kotlin.array2indexedMap
import com.mozhimen.emulatork.basic.archive.ArchiveState
import com.mozhimen.emulatork.basic.archive.ArchiveStateIncompatibleException
import com.mozhimen.emulatork.basic.load.LoadException
import com.mozhimen.emulatork.basic.load.SLoadError
import com.mozhimen.emulatork.basic.rom.SRomFileType
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.input.key.InputKey
import com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.basic.system.SystemSetting
import com.mozhimen.emulatork.common.core.CoreBundle
import com.mozhimen.emulatork.common.core.CorePropertyManager
import com.mozhimen.emulatork.common.game.GameBundle
import com.mozhimen.emulatork.common.game.GameLoadManager
import com.mozhimen.emulatork.common.game.SGameLoadState
import com.mozhimen.emulatork.common.input.GamepadConfigManager
import com.mozhimen.emulatork.common.input.RumbleManager
import com.mozhimen.emulatork.common.save.SaveManager
import com.mozhimen.emulatork.common.save.SaveStateManager
import com.mozhimen.emulatork.common.save.SaveStatePreviewManager
import com.mozhimen.emulatork.common.system.SystemBundle
import com.mozhimen.emulatork.common.system.SystemOption
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.emulatork.input.virtual.gamepad.GamepadConfig
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
import kotlin.math.abs
import kotlin.system.exitProcess
import com.mozhimen.emulatork.common.system.SystemProvider
import com.mozhimen.emulatork.core.property.CoreProperty
import com.mozhimen.emulatork.input.utils.getInputUnit
import com.mozhimen.emulatork.input.virtual.menu.MenuContract
import com.mozhimen.emulatork.input.utils.getInputKeyMap
import com.mozhimen.emulatork.core.option.CoreOption
import com.mozhimen.emulatork.core.utils.toCoreOption
import com.mozhimen.emulatork.input.virtual.screen.ShaderConfigProvider

/**
 * @ClassName BaseGameActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
@OptIn(FlowPreview::class, DelicateCoroutinesApi::class)
abstract class AbsGameActivity : com.mozhimen.emulatork.common.android.ImmersiveFragmentActivity(), IUtilK {

    companion object {
        const val DIALOG_REQUEST = 100

        const val EXTRA_GAME = "GAME"
        const val EXTRA_LOAD_SAVE = "LOAD_SAVE"
        const val EXTRA_LEANBACK = "LEANBACK"
        const val EXTRA_SYSTEM_CORE_CONFIG = "EXTRA_SYSTEM_CORE_CONFIG"

        const val REQUEST_PLAY_GAME = 1001
        const val PLAY_GAME_RESULT_SESSION_DURATION = "PLAY_GAME_RESULT_SESSION_DURATION"
        const val PLAY_GAME_RESULT_GAME = "PLAY_GAME_RESULT_GAME"
        const val PLAY_GAME_RESULT_LEANBACK = "PLAY_GAME_RESULT_LEANBACK"
        const val PLAY_GAME_RESULT_ERROR = "PLAY_GAME_RESULT_ERROR"

        const val RESULT_ERROR = Activity.RESULT_FIRST_USER + 2
        const val RESULT_UNEXPECTED_ERROR = Activity.RESULT_FIRST_USER + 3
    }

    data class SingleAxisEvent(val axis: Int, val action: Int, val keyCode: Int, val port: Int)

    //////////////////////////////////////////////////////////////////////////////////

    private lateinit var system: SystemBundle
    private lateinit var gameContainerLayout: FrameLayout
    private lateinit var loadingView: ProgressBar
    private lateinit var loadingMessageView: TextView
    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
    private val startGameTime = System.currentTimeMillis()
    private val keyEventsFlow: MutableSharedFlow<KeyEvent?> = MutableSharedFlow()
    private val motionEventsFlow: MutableSharedFlow<MotionEvent> = MutableSharedFlow()
    private val loadingState = MutableStateFlow(false)
    private val loadingMessageStateFlow = MutableStateFlow("")
    private val flowGamepadMap = MutableStateFlow<Map<Int, GamepadConfig>>(mapOf())

    //////////////////////////////////////////////////////////////////////////////////

    protected lateinit var game: Game
    protected lateinit var coreBundle: CoreBundle
    protected lateinit var mainContainerLayout: ConstraintLayout
    protected lateinit var leftGamePadContainer: FrameLayout
    protected lateinit var rightGamePadContainer: FrameLayout
    protected val retroGameViewFlow = MutableStateFlow<GLRetroView?>(null)
    protected var retroGameView: GLRetroView? by VarPropertyMutableState(retroGameViewFlow)

    //////////////////////////////////////////////////////////////////////////////////

    //    @Inject
//    lateinit var settingsManager: SettingsManager
    abstract fun settingManager(): SettingManager

    //    @Inject
//    lateinit var statesManager: StatesManager
    abstract fun saveStateManager(): SaveStateManager

    //    @Inject
//    lateinit var statesPreviewManager: StatesPreviewManager
    abstract fun saveStatePreviewManager(): SaveStatePreviewManager

    //    @Inject
//    lateinit var legacySavesManager: SavesManager
    abstract fun saveManager(): SaveManager

    //    @Inject
//    lateinit var coreVariablesManager: CoreVariablesManager
    abstract fun corePropertyManager(): CorePropertyManager

    //    @Inject
//    lateinit var inputDeviceManager: InputDeviceManager
    abstract fun inputUnitManager(): InputUnitManager

    //    @Inject
//    lateinit var gameLoader: GameLoader
    abstract fun gameLoadManager(): GameLoadManager

    //    @Inject
//    lateinit var controllerConfigsManager: ControllerConfigsManager
    abstract fun gamepadConfigManager(): GamepadConfigManager

    //    @Inject
//    lateinit var rumbleManager: RumbleManager
    abstract fun rumbleManager(): RumbleManager

    protected abstract fun gameMenuActivityClazz(): Class<out Activity>

    //////////////////////////////////////////////////////////////////////////////////

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
        coreBundle = intent.getSerializableExtra(EXTRA_SYSTEM_CORE_CONFIG) as CoreBundle
        system = SystemProvider.findSysByName(game.systemName)

        lifecycleScope.launch {
            loadGame()
        }

        initialiseFlows()
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
        if (event != null && InputKey(keyCode) in event.device.getInputUnit().getInputKeyMap()) {
            lifecycleScope.launch {
                keyEventsFlow.emit(event)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && InputKey(keyCode) in event.device.getInputUnit().getInputKeyMap()) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DIALOG_REQUEST) {
            Timber.i("Game menu dialog response: ${data?.extras.getStrDump()}")
            if (data?.getBooleanExtra(MenuContract.RESULT_RESET, false) == true) {
                GlobalScope.launch {
                    reset()
                }
            }
            if (data?.hasExtra(MenuContract.RESULT_SAVE) == true) {
                GlobalScope.launch {
                    saveSlot(data.getIntExtra(MenuContract.RESULT_SAVE, 0))
                }
            }
            if (data?.hasExtra(MenuContract.RESULT_LOAD) == true) {
                GlobalScope.launch {
                    loadSlot(data.getIntExtra(MenuContract.RESULT_LOAD, 0))
                }
            }
            if (data?.getBooleanExtra(MenuContract.RESULT_QUIT, false) == true) {
                GlobalScope.launch {
                    autoSaveAndFinish()
                }
            }
            if (data?.hasExtra(MenuContract.RESULT_CHANGE_DISK) == true) {
                val index = data.getIntExtra(MenuContract.RESULT_CHANGE_DISK, 0)
                retroGameView?.changeDisk(index)
            }
            if (data?.hasExtra(MenuContract.RESULT_ENABLE_AUDIO) == true) {
                retroGameView?.apply {
                    this.audioEnabled = data.getBooleanExtra(
                        MenuContract.RESULT_ENABLE_AUDIO,
                        true
                    )
                }
            }
            if (data?.hasExtra(MenuContract.RESULT_ENABLE_FAST_FORWARD) == true) {
                retroGameView?.apply {
                    val fastForwardEnabled = data.getBooleanExtra(
                        MenuContract.RESULT_ENABLE_FAST_FORWARD,
                        false
                    )
                    this.frameSpeed = if (fastForwardEnabled) 2 else 1
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun getControllerType(): Flow<Map<Int, GamepadConfig>> {
        return flowGamepadMap
    }

    protected fun displayOptionsDialog() {
        if (loadingState.value) return

        val coreOptions = getCoreOptions()

        val systemOptions: List<SystemOption> = coreBundle.systemSettings_exposed
            .mapNotNull { transformExposedSetting(it, coreOptions) }

        val systemOptionsAdvanced: List<SystemOption> = coreBundle.systemSettings_exposedAdvanced
            .mapNotNull { transformExposedSetting(it, coreOptions) }

        val intent = Intent(this, gameMenuActivityClazz()).apply {
            this.putExtra(MenuContract.EXTRA_SYSTEM_OPTIONS, systemOptions.toTypedArray())
            this.putExtra(MenuContract.EXTRA_SYSTEM_OPTIONS_ADVANCED, systemOptionsAdvanced.toTypedArray())
            this.putExtra(MenuContract.EXTRA_CURRENT_DISK, retroGameView?.getCurrentDisk() ?: 0)
            this.putExtra(MenuContract.EXTRA_AVAILABLE_DISKS, retroGameView?.getAvailableDisks() ?: 0)
            this.putExtra(MenuContract.EXTRA_GAME, game)
            this.putExtra(MenuContract.EXTRA_SYSTEM_CORE_BUNDLE, coreBundle)
            this.putExtra(MenuContract.EXTRA_AUDIO_ENABLED, retroGameView?.audioEnabled)
            this.putExtra(MenuContract.EXTRA_FAST_FORWARD_SUPPORTED, system.fastForwardSupport)
            this.putExtra(MenuContract.EXTRA_FAST_FORWARD, (retroGameView?.frameSpeed ?: 1) > 1)
        }
        startActivityForResult(intent, DIALOG_REQUEST)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    open fun onFinishTriggered() {}

    //////////////////////////////////////////////////////////////////////////////////

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
            val controllers = gamepadConfigManager().getGamepadConfigs(system.eSystemType, coreBundle)
            flowGamepadMap.value = controllers
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
            val options = corePropertyManager().getOptionsForCore(system.eSystemType, coreBundle)
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
        flowGamepadMap.collectSafe {
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
        rumbleManager().collectAndProcessRumbleEvents(coreBundle, rumbleEvents)
    }

    private fun setUpExceptionsHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            performUnexpectedErrorFinish(exception)
            defaultExceptionHandler?.uncaughtException(thread, exception)
        }
    }

    /* On some cores unserialize fails with no reason. So we need to try multiple times. */
    private suspend fun restoreAutoSaveAsync(archiveState: ArchiveState) {
        // PPSSPP and Mupen64 initialize some state while rendering the first frame, so we have to wait before restoring
        // the autosave. Do not change thread here. Stick to the GL one to avoid issues with PPSSPP.
        if (!isAutoSaveEnabled()) return

        try {
            waitGLEvent<GLRetroView.GLRetroEvents.FrameRendered>()
            restoreQuickSave(archiveState)
        } catch (e: Throwable) {
            Timber.e(e, "Error while loading auto-save")
        }
    }

    private suspend fun takeScreenshotPreview(index: Int) {
        val sizeInDp = SaveStatePreviewManager.PREVIEW_SIZE_DP
        val previewSize = sizeInDp.dp2pxI()
        val preview = retroGameView?.takeScreenshotOnMain(previewSize, 3)
        if (preview != null) {
            saveStatePreviewManager().setPreviewForSlot(game, preview, coreBundle.eCoreType, index)
        }
    }

    private fun initializeRetroGameView(
        gameBundle: GameBundle,
        hdMode: Boolean,
        forceLegacyHdMode: Boolean,
        screenFilter: String,
        lowLatencyAudio: Boolean,
        enableRumble: Boolean
    ): GLRetroView {
        val data = GLRetroViewData(this).apply {
            coreFilePath = gameBundle.coreLibrary

            when (val gameFiles = gameBundle.gameFiles) {
                is SRomFileType.Standard -> {
                    gameFilePath = gameFiles.files.first().absolutePath
                }

                is SRomFileType.Virtual -> {
                    gameVirtualFiles = gameFiles.files
                        .map { VirtualFile(it.filePath, it.fileDescriptor) }
                }
            }

            systemDirectory = gameBundle.systemDirectory.absolutePath
            savesDirectory = gameBundle.savesDirectory.absolutePath
            variables = gameBundle.coreProperties.map { Variable(it.key, it.value) }.toTypedArray()
            saveRAMState = gameBundle.saveRAMData
            shader = ShaderConfigProvider.getShaderBySystem(
                applicationContext,
                hdMode,
                forceLegacyHdMode,
                screenFilter,
                system.eSystemType
            )
            preferLowLatencyAudio = lowLatencyAudio
            rumbleEventsEnabled = enableRumble
            skipDuplicateFrames = coreBundle.skipDuplicateFrames
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
            gameBundle.quickSaveData?.let {
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

    private fun updateControllers(gamepadConfigMap: Map<Int, GamepadConfig>) {
        retroGameView
            ?.getControllers()?.array2indexedMap()
            ?.zipOnKeys(gamepadConfigMap, this::findControllerId)
            ?.filterNotNullValues()
            ?.forEach { (port, controllerId) ->
                Timber.i("Controls setting $port to $controllerId")
                retroGameView?.setControllerType(port, controllerId)
            }
    }

    private fun findControllerId(supported: Array<Controller>, gamepadConfig: GamepadConfig): Int? {
        return supported
            .firstOrNull { controller ->
                sequenceOf(
                    controller.id == gamepadConfig.libretroId,
                    controller.description == gamepadConfig.libretroDescriptor
                ).any { it }
            }?.id
    }

    private fun handleRetroViewError(errorCode: Int) {
        Timber.e("Error in GLRetroView $errorCode")
        val gameLoaderError = when (errorCode) {
            GLRetroView.ERROR_GL_NOT_COMPATIBLE -> SLoadError.GLIncompatible
            GLRetroView.ERROR_LOAD_GAME -> SLoadError.LoadGame
            GLRetroView.ERROR_LOAD_LIBRARY -> SLoadError.LoadCore
            GLRetroView.ERROR_SERIALIZATION -> SLoadError.Saves
            else -> SLoadError.Generic
        }
        retroGameView = null
        displayGameLoaderError(gameLoaderError, coreBundle)
    }

    private fun transformExposedSetting(systemExposedSetting: SystemSetting, coreOptions: List<CoreOption>): SystemOption? {
        return coreOptions
            .firstOrNull { it.coreProperty.key == systemExposedSetting.key }
            ?.let { SystemOption(systemExposedSetting, it) }
    }

    private suspend fun isAutoSaveEnabled(): Boolean {
        return coreBundle.isSupportStates && settingManager().autoSave()
    }

    private fun getCoreOptions(): List<CoreOption> {
        return retroGameView?.getVariables()
            ?.map { it.toCoreOption() } ?: listOf()
    }

    private fun updateCoreVariables(options: List<CoreProperty>) {
        val updatedVariables = options.map { Variable(it.key, it.value) }
            .toTypedArray()

        updatedVariables.forEach {
            Timber.i("Updating core variable: ${it.key} ${it.value}")
        }

        retroGameView?.updateVariables(*updatedVariables)
    }

    // Now that we wait for the first rendered frame this is probably no longer needed, but we'll keep it just to be sure
    private suspend fun restoreQuickSave(archiveState: ArchiveState) {
        var times = 10

        while (!loadArchiveState(archiveState) && times > 0) {
            delay(200)
            times--
        }
    }

    private suspend fun initializeGamePadShortcutsFlow() {
        inputUnitManager().getInputMenuShortCutObservable()
            .distinctUntilChanged()
            .collectSafe { shortcut ->
                shortcut?.let {
                    resources.getString(R.string.game_toast_settings_button_using_gamepad, it.name).showToast()
                }
            }
    }

    private suspend fun initializeVirtualGamePadMotionsFlow() {
        val events = combine(
            inputUnitManager().getGamePadsPortMapperObservable(),
            motionEventsFlow,
            ::NTuple2
        )

        events
            .mapNotNull { (ports, event) ->
                ports(event.device)?.let { it to event }
            }
            .map { (port, event) ->
                val axes = event.device.getInputKeyMap().getAxesMap().entries

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
            inputUnitManager().getGamePadsPortMapperObservable(),
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

        val shortcutKeys = inputUnitManager().getInputMenuShortCutObservable()
            .map { it?.keys ?: setOf() }

        val combinedObservable = combine(
            shortcutKeys,
            inputUnitManager().getGamePadsPortMapperObservable(),
            inputUnitManager().getInputBindingsObservable(),
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
                if (flowGamepadMap.value[port]?.mergeDPADAndLeftStickEvents == true) {
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

    private suspend fun saveAutoSave(game: Game) {
        if (!isAutoSaveEnabled()) return
        val state = getCurrentSaveState()

        if (state != null) {
            saveStateManager().setAutoSave(game, coreBundle.eCoreType, state)
            Timber.i("Stored autosave file with size: ${state?.state?.size}")
        }
    }

    private suspend fun saveSRAM(game: Game) {
        val retroGameView = retroGameView ?: return
        val sramState = retroGameView.serializeSRAM()
        saveManager().setSaveRAM(game, sramState)
        Timber.i("Stored sram file with size: ${sramState.size}")
    }

    private suspend fun saveSlot(index: Int) {
        if (loadingState.value) return
        withLoading {
            getCurrentSaveState()?.let {
                saveStateManager().setSlotSave(game, it, coreBundle.eCoreType, index)
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
                saveStateManager().getSlotSave(game, coreBundle.eCoreType, index)?.let {
                    val loaded = withContext(Dispatchers.IO) {
                        loadArchiveState(it)
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

    private fun getCurrentSaveState(): ArchiveState? {
        val retroGameView = retroGameView ?: return null
        val currentDisk = if (system.hasMultiDiskSupport) {
            retroGameView.getCurrentDisk()
        } else {
            0
        }
        return ArchiveState(
            retroGameView.serializeState(),
            ArchiveState.ArchiveMetadata(currentDisk, coreBundle.statesVersion)
        )
    }

    private fun loadArchiveState(archiveState: ArchiveState): Boolean {
        val retroGameView = retroGameView ?: return false

        if (coreBundle.statesVersion != archiveState.metadata.version) {
            throw ArchiveStateIncompatibleException()
        }

        if (system.hasMultiDiskSupport &&
            retroGameView.getAvailableDisks() > 1 &&
            retroGameView.getCurrentDisk() != archiveState.metadata.diskIndex
        ) {
            retroGameView.changeDisk(archiveState.metadata.diskIndex)
        }

        return retroGameView.unserializeState(archiveState.state)
    }

    private suspend fun displayLoadStateErrorMessage(throwable: Throwable) = withContext(Dispatchers.Main) {
        when (throwable) {
            is ArchiveStateIncompatibleException ->
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

    private suspend fun loadGame() {
        val requestLoadSave = intent.getBooleanExtra(EXTRA_LOAD_SAVE, false)

        val autoSaveEnabled = settingManager().autoSave()
        val filter = settingManager().screenFilter()
        val hdMode = settingManager().hdMode()
        val forceLegacyHdMode = settingManager().forceLegacyHdMode()
        val lowLatencyAudio = settingManager().lowLatencyAudio()
        val enableRumble = settingManager().enableRumble()
        val directLoad = settingManager().allowDirectGameLoad()

        val loadingStatesFlow = gameLoadManager().load(
            applicationContext,
            game,
            requestLoadSave && autoSaveEnabled,
            coreBundle,
            directLoad
        )

        loadingStatesFlow
            .flowOn(Dispatchers.IO)
            .catch {
                displayGameLoaderError((it as LoadException).sLoadError, coreBundle)
            }
            .collect { loadingState ->
                displayLoadingState(loadingState)
                if (loadingState is SGameLoadState.LoadReady) {
                    retroGameView = initializeRetroGameView(
                        loadingState.gameBundle,
                        hdMode,
                        forceLegacyHdMode,
                        filter,
                        lowLatencyAudio,
                        coreBundle.isSupportRumble && enableRumble
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

    private fun displayLoadingState(sGameLoadState: SGameLoadState) {
        loadingMessageStateFlow.value = when (sGameLoadState) {
            is SGameLoadState.LoadingCore -> getString(R.string.game_loading_download_core)
            is SGameLoadState.LoadingGame -> getString(R.string.game_loading_preparing_game)
            else -> ""
        }
    }

    private fun displayGameLoaderError(sLoadError: SLoadError, coreBundle: CoreBundle) {

        val messageId = when (sLoadError) {
            is SLoadError.GLIncompatible -> getString(R.string.game_loader_error_gl_incompatible)
            is SLoadError.Generic -> getString(R.string.game_loader_error_generic)
            is SLoadError.LoadCore -> getString(R.string.game_loader_error_load_core)
            is SLoadError.LoadGame -> getString(R.string.game_loader_error_load_game)
            is SLoadError.Saves -> getString(R.string.game_loader_error_save)
            is SLoadError.UnsupportedArchitecture -> getString(R.string.game_loader_error_unsupported_architecture)
            is SLoadError.MissingBiosFiles -> getString(
                R.string.game_loader_error_missing_bios,
                sLoadError.missingFiles
            )
        }

        performErrorFinish(messageId)
    }
}
