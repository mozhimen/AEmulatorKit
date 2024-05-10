package com.mozhimen.emulatork.test.feature.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mozhimen.basick.utilk.android.view.applyVisibleIfElseGone
import com.mozhimen.emulatork.basic.injection.PerActivity
import com.mozhimen.emulatork.basic.injection.PerFragment
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.feature.settings.SettingsFragment
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.feature.favorites.FavoritesFragment
import com.mozhimen.emulatork.test.feature.games.GamesFragment
import com.mozhimen.emulatork.test.feature.games.SystemsFragment
import com.mozhimen.emulatork.test.feature.home.HomeFragment
import com.mozhimen.emulatork.test.feature.search.SearchFragment
import com.mozhimen.emulatork.test.feature.settings.SettingsInteractor
import com.mozhimen.emulatork.test.shared.GameInteractor
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
class MainActivity : com.mozhimen.emulatork.basic.android.RetrogradeAppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeActivity()
    }

    private fun initializeActivity() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        val topLevelIds = setOf(
            R.id.navigation_home,
            R.id.navigation_favorites,
            R.id.navigation_search,
            R.id.navigation_systems,
            R.id.navigation_settings
        )
        val appBarConfiguration = AppBarConfiguration(topLevelIds)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val mainViewModel = ViewModelProviders.of(this, MainViewModel.Factory(applicationContext))
            .get(MainViewModel::class.java)

        mainViewModel.indexingInProgress.observe(this, Observer { isRunning ->
            findViewById<MaterialProgressBar>(R.id.progress).applyVisibleIfElseGone(isRunning)
        })
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    @dagger.Module
    abstract class Module {

        @PerFragment
        @ContributesAndroidInjector(modules = [SettingsFragment.Module::class])
        abstract fun settingsFragment(): SettingsFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [GamesFragment.Module::class])
        abstract fun gamesFragment(): GamesFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [SystemsFragment.Module::class])
        abstract fun systemsFragment(): SystemsFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [HomeFragment.Module::class])
        abstract fun homeFragment(): HomeFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [SearchFragment.Module::class])
        abstract fun searchFragment(): SearchFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [FavoritesFragment.Module::class])
        abstract fun favoritesFragment(): FavoritesFragment

        @dagger.Module
        companion object {

            @Provides
            @PerActivity
            @JvmStatic
            fun settingsInteractor(activity: MainActivity) =
                SettingsInteractor(activity)

            @Provides
            @PerActivity
            @JvmStatic
            fun gameInteractor(activity: MainActivity, retrogradeDb: RetrogradeDatabase) =
                GameInteractor(activity, retrogradeDb)

            @Provides
            @PerActivity
            @JvmStatic
            fun rxPermissions(activity: MainActivity): RxPermissions = RxPermissions(activity)
        }
    }
}