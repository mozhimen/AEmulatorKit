package com.mozhimen.emulatork.ui.hilt.game

import com.mozhimen.emulatork.common.core.CoreSelectionManager
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.ui.game.AbsExternalGameLauncherActivity
import com.mozhimen.emulatork.ext.game.GameLauncher
import com.mozhimen.emulatork.ext.game.GameLaunchTaskHandler
import com.mozhimen.emulatork.ui.hilt.works.WorkSaveSync
import com.mozhimen.emulatork.ui.hilt.works.WorkStorageCacheCleaner
import com.mozhimen.emulatork.ui.works.AbsWorkSaveSync
import com.mozhimen.emulatork.ui.works.AbsWorkStorageCacheCleaner
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
class ExternalGameLauncherActivity :AbsExternalGameLauncherActivity() {
    @Inject
    lateinit var retrogradeDatabase: RetrogradeDatabase

    @Inject
    lateinit var gameLaunchTaskHandler: GameLaunchTaskHandler

    @Inject
    lateinit var coreSelectionManager: CoreSelectionManager

    @Inject
    lateinit var gameLauncher: GameLauncher

    override fun retrogradeDatabase(): RetrogradeDatabase {
        return retrogradeDatabase
    }

    override fun gameLaunchTaskHandler(): GameLaunchTaskHandler {
        return gameLaunchTaskHandler
    }

    override fun coreSelectionManager(): CoreSelectionManager {
        return coreSelectionManager
    }

    override fun gameLauncher(): GameLauncher {
        return gameLauncher
    }

    override fun gameActivityClazz(): Class<*> {
        return GameActivity::class.java
    }

    override fun workSaveSyncClazz(): Class<out AbsWorkSaveSync> {
        return WorkSaveSync::class.java
    }

    override fun workStorageCacheCleanerClazz(): Class<out AbsWorkStorageCacheCleaner> {
        return WorkStorageCacheCleaner::class.java
    }
}