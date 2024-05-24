package com.mozhimen.emulatork.test.hilt.game

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import com.mozhimen.emulatork.basic.game.GameLoader
import com.mozhimen.emulatork.basic.save.SaveManager
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.save.SaveStatePreviewManager
import com.mozhimen.emulatork.test.hilt.game.menu.GameMenuActivity
import com.mozhimen.emulatork.ui.game.AbsGameActivity
import com.mozhimen.emulatork.ui.game.AbsGameService
import com.mozhimen.emulatork.input.device.InputDeviceManager
import com.mozhimen.emulatork.basic.game.rumble.GameRumbleManager
import com.mozhimen.emulatork.basic.controller.ControllerConfigsManager
import com.mozhimen.emulatork.basic.game.setting.GameSettingsManager
import javax.inject.Inject
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint

/**
 * @ClassName GameActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
@AndroidEntryPoint
class GameActivity : AbsGameActivity() {
    @Inject
    lateinit var gameSettingsManager: GameSettingsManager

    @Inject
    lateinit var saveStateManager: SaveStateManager

    @Inject
    lateinit var saveStatePreviewManager: SaveStatePreviewManager

    @Inject
    lateinit var legacySaveManager: SaveManager

    @Inject
    lateinit var coreVariablesManager: CoreVariablesManager

    @Inject
    lateinit var inputDeviceManager: InputDeviceManager

    @Inject
    lateinit var gameLoader: GameLoader

    @Inject
    lateinit var controllerConfigsManager: ControllerConfigsManager

    @Inject
    lateinit var gameRumbleManager: GameRumbleManager

    @Inject
    lateinit var sharedPreferences: Lazy<SharedPreferences>

    override fun sharedPreferences(): kotlin.Lazy<SharedPreferences> {
        return lazy { sharedPreferences.get() }
    }

    override fun gameMenuActivityClazz(): Class<out Activity> {
        return GameMenuActivity::class.java
    }

    override fun gameServiceClass(): Class<out AbsGameService> {
        return GameService::class.java
    }

    override fun settingsManager(): GameSettingsManager {
        return gameSettingsManager
    }

    override fun statesManager(): SaveStateManager {
        return saveStateManager
    }

    override fun statesPreviewManager(): SaveStatePreviewManager {
        return saveStatePreviewManager
    }

    override fun legacySavesManager(): SaveManager {
        return legacySaveManager
    }

    override fun coreVariablesManager(): CoreVariablesManager {
        return coreVariablesManager
    }

    override fun inputDeviceManager(): InputDeviceManager {
        return inputDeviceManager
    }

    override fun gameLoader(): GameLoader {
        return gameLoader
    }

    override fun controllerConfigsManager(): ControllerConfigsManager {
        return controllerConfigsManager
    }

    override fun rumbleManager(): GameRumbleManager {
        return gameRumbleManager
    }
}