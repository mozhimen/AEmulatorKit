package com.mozhimen.emulatork.ui.dagger.gamemenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mozhimen.emulatork.basic.dagger.interfaces.PerFragment
import com.mozhimen.emulatork.ui.gamemenu.GameMenuActivity
import com.mozhimen.emulatork.ui.gamemenu.GameMenuCoreOptionsFragment
import com.mozhimen.emulatork.ui.gamemenu.GameMenuFragment
import com.mozhimen.emulatork.ui.gamemenu.GameMenuLoadFragment
import com.mozhimen.emulatork.ui.gamemenu.GameMenuSaveFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @ClassName GameMenuActivity
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 21:50
 * @Version 1.0
 */
class GameMenuActivity:GameMenuActivity() , HasFragmentInjector, HasSupportFragmentInjector {
    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? = supportFragmentInjector

    override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? = frameworkFragmentInjector

    @dagger.Module
    abstract class Module {

        @PerFragment
        @ContributesAndroidInjector(modules = [GameMenuCoreOptionsFragment.Module::class])
        abstract fun coreOptionsFragment(): GameMenuCoreOptionsFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [GameMenuFragment.Module::class])
        abstract fun gameMenuFragment(): GameMenuFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [GameMenuLoadFragment.Module::class])
        abstract fun gameMenuLoadFragment(): GameMenuLoadFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [GameMenuSaveFragment.Module::class])
        abstract fun gameMenuSaveFragment(): GameMenuSaveFragment
    }
}