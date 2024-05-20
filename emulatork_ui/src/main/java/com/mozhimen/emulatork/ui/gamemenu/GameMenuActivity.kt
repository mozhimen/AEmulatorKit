package com.mozhimen.emulatork.ui.gamemenu

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.mozhimen.emulatork.basic.dagger.interfaces.PerFragment
import com.mozhimen.emulatork.basic.dagger.android.RetrogradeAppCompatActivity
import com.mozhimen.emulatork.ui.R
import dagger.android.ContributesAndroidInjector

/**
 * @ClassName GameMenuActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class GameMenuActivity : RetrogradeAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_navigation_overlay)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(com.mozhimen.emulatork.ui.dagger.R.navigation.mobile_game_menu, intent.extras)

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

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
