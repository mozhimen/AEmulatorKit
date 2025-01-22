package com.mozhimen.emulatork.ui.hilt.game

import android.app.Activity
import android.content.SharedPreferences
import com.mozhimen.emulatork.ui.hilt.game.menu.GameMenuActivity
import com.mozhimen.emulatork.ui.game.AbsGameActivity
import com.mozhimen.emulatork.ui.game.AbsGameService
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.common.core.CorePropertyManager
import com.mozhimen.emulatork.common.input.GamepadConfigManager
import com.mozhimen.emulatork.common.input.RumbleManager
import com.mozhimen.emulatork.common.save.SaveManager
import com.mozhimen.emulatork.common.save.SaveStateManager
import com.mozhimen.emulatork.common.save.SaveStatePreviewManager
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
    lateinit var settingsManager: SettingManager

    @Inject
    lateinit var saveStateManager: SaveStateManager

    @Inject
    lateinit var saveStatePreviewManager: SaveStatePreviewManager

    @Inject
    lateinit var legacySaveManager: SaveManager

    @Inject
    lateinit var corePropertyManager: CorePropertyManager

    @Inject
    lateinit var inputUnitManager: InputUnitManager

    @Inject
    lateinit var gameLoadManager: com.mozhimen.emulatork.common.game.GameLoadManager

    @Inject
    lateinit var gamepadConfigManager: GamepadConfigManager

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

    override fun settingManager(): SettingManager {
        return settingsManager
    }

    override fun saveStateManager(): SaveStateManager {
        return saveStateManager
    }

    override fun saveStatePreviewManager(): SaveStatePreviewManager {
        return saveStatePreviewManager
    }

    override fun saveManager(): SaveManager {
        return legacySaveManager
    }

    override fun corePropertyManager(): CorePropertyManager {
        return corePropertyManager
    }

    override fun inputUnitManager(): InputUnitManager {
        return inputUnitManager
    }

    override fun gameLoadManager(): com.mozhimen.emulatork.common.game.GameLoadManager {
        return gameLoadManager
    }

    override fun gamepadConfigManager(): GamepadConfigManager {
        return gamepadConfigManager
    }

    override fun rumbleManager(): RumbleManager {
        return rumbleManager
    }
}