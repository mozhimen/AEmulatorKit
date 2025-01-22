package com.mozhimen.emulatork.ext.game

import android.app.Activity
import com.mozhimen.emulatork.common.core.CoreSelectionManager
import com.mozhimen.emulatork.common.system.SystemProvider
import com.mozhimen.emulatork.db.game.entities.Game
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
    private val coreSelectionManager: CoreSelectionManager,
    private val gameLaunchTaskHandler: GameLaunchTaskHandler
) {

    @OptIn(DelicateCoroutinesApi::class)
    fun launchGameAsync(activity: Activity, gameActivityClazz: Class<*>, game: Game, loadSave: Boolean, leanback: Boolean) {
        GlobalScope.launch {
            val system = SystemProvider.findSysByName(game.systemName)
            val coreConfig = coreSelectionManager.getCoreConfigForSystem(system)
            gameLaunchTaskHandler.handleGameStart(activity.applicationContext)
            AbsGameActivity.launchGame(activity, gameActivityClazz, coreConfig, game, loadSave, leanback)
        }
    }
}
