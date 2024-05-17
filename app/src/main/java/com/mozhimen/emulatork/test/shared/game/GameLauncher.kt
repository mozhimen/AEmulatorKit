package com.mozhimen.emulatork.test.shared.game

import android.app.Activity
import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.test.shared.main.GameLaunchTaskHandler
import com.mozhimen.emulatork.ui.dagger.shared.game.BaseGameActivity
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
    private val coresSelection: CoresSelection,
    private val gameLaunchTaskHandler: GameLaunchTaskHandler
) {

    @OptIn(DelicateCoroutinesApi::class)
    fun launchGameAsync(activity: Activity, game: Game, loadSave: Boolean, leanback: Boolean) {
        GlobalScope.launch {
            val system = GameSystem.findById(game.systemId)
            val coreConfig = coresSelection.getCoreConfigForSystem(system)
            gameLaunchTaskHandler.handleGameStart(activity.applicationContext)
            BaseGameActivity.launchGame(activity, coreConfig, game, loadSave, leanback)
        }
    }
}
