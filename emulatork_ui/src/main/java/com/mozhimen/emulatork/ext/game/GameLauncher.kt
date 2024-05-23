package com.mozhimen.emulatork.ext.game

import android.app.Activity
import com.mozhimen.emulatork.basic.core.CoreSelection
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.game.system.GameSystems
import com.mozhimen.emulatork.ui.game.AbsGameActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @ClassName GameLauncher
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameLauncher constructor(
    private val coresSelection: CoreSelection,
    private val gameLaunchTaskHandler: GameLaunchTaskHandler
) {

    @OptIn(DelicateCoroutinesApi::class)
    fun launchGameAsync(activity: Activity, gameActivityClazz: Class<*>, game: Game, loadSave: Boolean, leanback: Boolean) {
        GlobalScope.launch {
            val system = GameSystems.findById(game.systemId)
            val coreConfig = coresSelection.getCoreConfigForSystem(system)
            gameLaunchTaskHandler.handleGameStart(activity.applicationContext)
            AbsGameActivity.launchGame(activity, gameActivityClazz, coreConfig, game, loadSave, leanback)
        }
    }
}
