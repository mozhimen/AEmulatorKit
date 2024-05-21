package com.mozhimen.emulatork.ui.hilt.game

import android.app.Activity
import android.content.SharedPreferences
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import com.mozhimen.emulatork.basic.game.GameLoader
import com.mozhimen.emulatork.basic.saves.SavesManager
import com.mozhimen.emulatork.basic.saves.StatesManager
import com.mozhimen.emulatork.basic.saves.StatesPreviewManager
import com.mozhimen.emulatork.ui.hilt.gamemenu.GameMenuActivity
import com.mozhimen.emulatork.ui.game.AbsGameActivity
import com.mozhimen.emulatork.ui.game.AbsGameService
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import com.mozhimen.emulatork.ui.rumble.RumbleManager
import com.mozhimen.emulatork.ui.settings.ControllerConfigsManager
import com.mozhimen.emulatork.ui.settings.SettingsManager
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

    override fun settingsManager(): SettingsManager {
        return settingsManager
    }

    override fun statesManager(): StatesManager {
        return statesManager
    }

    override fun statesPreviewManager(): StatesPreviewManager {
        return statesPreviewManager
    }

    override fun legacySavesManager(): SavesManager {
        return legacySavesManager
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

    override fun rumbleManager(): RumbleManager {
        return rumbleManager
    }
}