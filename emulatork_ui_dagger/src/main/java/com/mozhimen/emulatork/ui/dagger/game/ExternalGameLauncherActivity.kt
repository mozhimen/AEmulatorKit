package com.mozhimen.emulatork.ui.dagger.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mozhimen.emulatork.basic.core.CoresSelection
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.ui.game.AbstractExternalGameLauncherActivity
import com.mozhimen.emulatork.ui.game.GameLauncher
import com.mozhimen.emulatork.ui.main.GameLaunchTaskHandler
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @ClassName ExternalGameLauncherActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/21
 * @Version 1.0
 */
class ExternalGameLauncherActivity :AbstractExternalGameLauncherActivity(), HasFragmentInjector, HasSupportFragmentInjector {
    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    @Inject
    lateinit var retrogradeDatabase: RetrogradeDatabase

    @Inject
    lateinit var gameLaunchTaskHandler: GameLaunchTaskHandler

    @Inject
    lateinit var coresSelection: CoresSelection

    @Inject
    lateinit var gameLauncher: GameLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? =
        supportFragmentInjector

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? =
        frameworkFragmentInjector

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