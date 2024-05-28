package com.mozhimen.emulatork.test.dagger.main

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
import com.mozhimen.basick.utilk.commons.IUtilK
import com.mozhimen.basick.utilk.kotlinx.coroutines.launchSafe
import com.mozhimen.emulatork.basic.dagger.annors.PerActivity
import com.mozhimen.emulatork.basic.dagger.annors.PerFragment
import com.mozhimen.emulatork.basic.dagger.android.DaggerAppCompatActivity
import com.mozhimen.emulatork.basic.game.system.GameSystemID
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManager
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager
import com.mozhimen.emulatork.basic.game.review.GameReviewManager
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.test.dagger.favorites.FavoritesFragment
import com.mozhimen.emulatork.test.dagger.games.GamesFragment
import com.mozhimen.emulatork.test.dagger.home.HomeFragment
import com.mozhimen.emulatork.test.dagger.search.SearchFragment
import com.mozhimen.emulatork.test.dagger.settings.AdvancedSettingsFragment
import com.mozhimen.emulatork.test.dagger.settings.BiosSettingsFragment
import com.mozhimen.emulatork.test.dagger.settings.CoresSelectionFragment
import com.mozhimen.emulatork.test.dagger.settings.GamepadSettingsFragment
import com.mozhimen.emulatork.test.dagger.settings.SaveSyncFragment
import com.mozhimen.emulatork.test.dagger.settings.SettingsFragment
import com.mozhimen.emulatork.test.dagger.systems.MetaSystemsFragment
import com.mozhimen.emulatork.ui.dagger.game.GameActivity
import com.mozhimen.emulatork.ext.library.SettingsInteractor
import com.mozhimen.emulatork.ext.covers.CoverShortcutGenerator
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.game.GameLauncher
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.basic.game.GameBusyActivity
import com.mozhimen.emulatork.ext.game.GameLaunchTaskHandler
import com.mozhimen.emulatork.ext.game.pad.GamePadPreferencesManager
import com.mozhimen.emulatork.ext.game.BaseGameActivity
import com.mozhimen.emulatork.ext.works.WorkScheduler
import com.mozhimen.emulatork.ui.dagger.game.pad.GamePadBindingActivity
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject
import com.mozhimen.emulatork.ui.dagger.works.WorkSaveSync
import com.mozhimen.emulatork.ui.dagger.works.WorkStorageCacheCleaner

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@OptIn(DelicateCoroutinesApi::class)
class MainActivity : DaggerAppCompatActivity(), GameBusyActivity ,IUtilK{

    @Inject
    lateinit var gameLaunchTaskHandler: GameLaunchTaskHandler

    @Inject
    lateinit var saveSyncManager: SaveSyncManager

    private val gameReviewManager = GameReviewManager()
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this)
        setContentView(com.mozhimen.emulatork.test.dagger.R.layout.activity_main)
        initializeActivity()
    }

    override fun activity(): Activity = this
    override fun isBusy(): Boolean = mainViewModel?.displayProgress?.value ?: false

    private fun initializeActivity() {
        setSupportActionBar(findViewById(R.id.toolbar))

        GlobalScope.launchSafe {
            gameReviewManager.initialize(applicationContext)
        }

        val navView: BottomNavigationView = findViewById(com.mozhimen.emulatork.test.dagger.R.id.nav_view)
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
                    gameLaunchTaskHandler.handleGameFinish(true, this@MainActivity, resultCode, data, WorkSaveSync::class.java, WorkStorageCacheCleaner::class.java)
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
                WorkScheduler.enqueueManualWork(WorkSaveSync::class.java, this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayLemuroidHelp() {
        val systemFolders = GameSystemID.values()
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
            fun settingsInteractor(activity: MainActivity, storageDirectoriesManager: StorageDirectoriesManager) =
                SettingsInteractor(activity, storageDirectoriesManager)

            @Provides
            @PerActivity
            @JvmStatic
            fun gamePadPreferencesHelper(inputUnitManager: InputUnitManager) =
                GamePadPreferencesManager(inputUnitManager, GamePadBindingActivity::class.java, false)

            @Provides
            @PerActivity
            @JvmStatic
            fun gameInteractor(
                activity: MainActivity,
                retrogradeDb: RetrogradeDatabase,
                coverShortcutGenerator: CoverShortcutGenerator,
                gameLauncher: GameLauncher
            ) =
                GameInteractor(activity, GameActivity::class.java, retrogradeDb, false, coverShortcutGenerator, gameLauncher)
        }
    }
}
