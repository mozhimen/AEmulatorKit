package com.mozhimen.emulatork.ui.hilt.game

import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.game.AbstractExternalGameLauncherActivity
import com.mozhimen.emulatork.ui.game.GameLauncher
import com.mozhimen.emulatork.ui.main.GameLaunchTaskHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @ClassName ExternalGameLauncherActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
@AndroidEntryPoint
class ExternalGameLauncherActivity :AbstractExternalGameLauncherActivity() {

    @Inject
    lateinit var retrogradeDatabase: RetrogradeDatabase

    @Inject
    lateinit var gameLaunchTaskHandler: GameLaunchTaskHandler

    @Inject
    lateinit var coresSelection: CoresSelection

    @Inject
    lateinit var gameLauncher: GameLauncher

    override fun retrogradeDatabase(): RetrogradeDatabase {
        return retrogradeDatabase
    }

    override fun gameLaunchTaskHandler(): GameLaunchTaskHandler {
        return gameLaunchTaskHandler
    }

    override fun coresSelection(): CoresSelection {
        return coresSelection
    }

    override fun gameLauncher(): GameLauncher {
        return gameLauncher
    }

    override fun gameActivityClazz(): Class<*> {
        return GameActivity::class.java
    }
}