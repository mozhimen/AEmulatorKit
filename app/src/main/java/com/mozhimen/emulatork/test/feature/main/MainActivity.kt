package com.mozhimen.emulatork.test.feature.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.elevation.SurfaceColors
import com.mozhimen.basick.utilk.kotlinx.coroutines.launchSafe
import com.mozhimen.emulatork.basic.dagger.PerActivity
import com.mozhimen.emulatork.basic.dagger.PerFragment
import com.mozhimen.emulatork.basic.library.SystemID
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.savesync.SaveSyncManager
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.test.feature.settings.SettingsFragment
import com.mozhimen.emulatork.test.R
import com.mozhimen.emulatork.test.feature.favorites.FavoritesFragment
import com.mozhimen.emulatork.test.feature.games.GamesFragment
import com.mozhimen.emulatork.test.feature.home.HomeFragment
import com.mozhimen.emulatork.test.feature.search.SearchFragment
import com.mozhimen.emulatork.test.feature.settings.AdvancedSettingsFragment
import com.mozhimen.emulatork.test.feature.settings.BiosSettingsFragment
import com.mozhimen.emulatork.test.feature.settings.CoresSelectionFragment
import com.mozhimen.emulatork.test.feature.settings.GamepadSettingsFragment
import com.mozhimen.emulatork.test.feature.settings.SaveSyncFragment
import com.mozhimen.emulatork.test.shared.settings.SettingsInteractor
import com.mozhimen.emulatork.test.feature.shortcuts.ShortcutsGenerator
import com.mozhimen.emulatork.test.feature.systems.MetaSystemsFragment
import com.mozhimen.emulatork.test.shared.GameInteractor
import com.mozhimen.emulatork.test.shared.game.GameLauncher
import com.mozhimen.emulatork.ui.dagger.shared.input.InputDeviceManager
import com.mozhimen.emulatork.test.shared.main.BusyActivity
import com.mozhimen.emulatork.test.shared.main.GameLaunchTaskHandler
import com.mozhimen.emulatork.test.shared.savesync.SaveSyncWork
import com.mozhimen.emulatork.test.shared.settings.GamePadPreferencesHelper
import com.mozhimen.emulatork.ui.dagger.shared.game.BaseGameActivity
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@OptIn(DelicateCoroutinesApi::class)
class MainActivity : RetrogradeAppCompatActivity(), BusyActivity {

    @Inject
    lateinit var gameLaunchTaskHandler: GameLaunchTaskHandler

    @Inject
    lateinit var saveSyncManager: SaveSyncManager

    private val reviewManager = com.mozhimen.emulatork.ui.review.ReviewManager()
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this)
        setContentView(R.layout.activity_main)
        initializeActivity()
    }

    override fun activity(): Activity = this
    override fun isBusy(): Boolean = mainViewModel?.displayProgress?.value ?: false

    private fun initializeActivity() {
        setSupportActionBar(findViewById(R.id.toolbar))

        GlobalScope.launchSafe {
            reviewManager.initialize(applicationContext)
        }

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

        val factory = MainViewModel.Factory(applicationContext)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        mainViewModel?.displayProgress?.observe(this) { isRunning ->
            findViewById<ProgressBar>(R.id.progress).isVisible = isRunning
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            BaseGameActivity.REQUEST_PLAY_GAME -> {
                GlobalScope.launchSafe {
                    gameLaunchTaskHandler.handleGameFinish(true, this@MainActivity, resultCode, data)
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isSupported = saveSyncManager.isSupported()
        val isConfigured = saveSyncManager.isConfigured()
        menu.findItem(R.id.menu_options_sync)?.isVisible = isSupported && isConfigured
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mobile_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_options_help -> {
                displayLemuroidHelp()
                true
            }
            R.id.menu_options_sync -> {
                SaveSyncWork.enqueueManualWork(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayLemuroidHelp() {
        val systemFolders = SystemID.values()
            .map { it.dbname }
            .map { "<i>$it</i>" }
            .joinToString(", ")

        val message = getString(R.string.lemuroid_help_content).replace("\$SYSTEMS", systemFolders)
        AlertDialog.Builder(this)
            .setMessage(Html.fromHtml(message))
            .show()
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
        @ContributesAndroidInjector(modules = [MetaSystemsFragment.Module::class])
        abstract fun systemsFragment(): MetaSystemsFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [HomeFragment.Module::class])
        abstract fun homeFragment(): HomeFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [SearchFragment.Module::class])
        abstract fun searchFragment(): SearchFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [FavoritesFragment.Module::class])
        abstract fun favoritesFragment(): FavoritesFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [GamepadSettingsFragment.Module::class])
        abstract fun gamepadSettings(): GamepadSettingsFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [BiosSettingsFragment.Module::class])
        abstract fun biosInfoFragment(): BiosSettingsFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [AdvancedSettingsFragment.Module::class])
        abstract fun advancedSettingsFragment(): AdvancedSettingsFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [SaveSyncFragment.Module::class])
        abstract fun saveSyncFragment(): SaveSyncFragment

        @PerFragment
        @ContributesAndroidInjector(modules = [CoresSelectionFragment.Module::class])
        abstract fun coresSelectionFragment(): CoresSelectionFragment

        @dagger.Module
        companion object {

            @Provides
            @PerActivity
            @JvmStatic
            fun settingsInteractor(activity: MainActivity, directoriesManager: DirectoriesManager) =
                SettingsInteractor(activity, directoriesManager)

            @Provides
            @PerActivity
            @JvmStatic
            fun gamePadPreferencesHelper(inputDeviceManager: InputDeviceManager) =
                GamePadPreferencesHelper(inputDeviceManager, false)

            @Provides
            @PerActivity
            @JvmStatic
            fun gameInteractor(
                activity: MainActivity,
                retrogradeDb: RetrogradeDatabase,
                shortcutsGenerator: ShortcutsGenerator,
                gameLauncher: GameLauncher
            ) =
                GameInteractor(activity, retrogradeDb, false, shortcutsGenerator, gameLauncher)
        }
    }
}
