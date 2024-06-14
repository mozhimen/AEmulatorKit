package com.mozhimen.emulatork.test.hilt.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.mozhimen.emulatork.basic.system.ESystemType
import com.mozhimen.emulatork.common.android.BusyProvider
import com.mozhimen.emulatork.common.archive.ArchiveManager
import com.mozhimen.emulatork.ext.game.AbsGameActivity
import com.mozhimen.emulatork.ext.game.GameLaunchTaskHandler
import com.mozhimen.emulatork.ext.works.WorkScheduler
import com.mozhimen.emulatork.ui.R
import com.mozhimen.emulatork.ui.hilt.works.WorkSaveSync
import com.mozhimen.emulatork.ui.hilt.works.WorkStorageCacheCleaner
import dagger.hilt.android.AndroidEntryPoint
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
@AndroidEntryPoint
@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity(), BusyProvider,IUtilK {

    @Inject
    lateinit var gameLaunchTaskHandler: GameLaunchTaskHandler

    @Inject
    lateinit var saveSyncManager: ArchiveManager

    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this)
        setContentView(com.mozhimen.emulatork.test.hilt.R.layout.activity_main)
        initializeActivity()
    }

    override fun activity(): Activity = this
    override fun isBusy(): Boolean = mainViewModel?.displayProgress?.value ?: false

    private fun initializeActivity() {
        setSupportActionBar(findViewById(R.id.toolbar))

//        GlobalScope.launchSafe {
//            gameReviewManager.initialize(applicationContext)
//        }

        val navView: BottomNavigationView = findViewById(com.mozhimen.emulatork.test.hilt.R.id.nav_view)
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
            AbsGameActivity.REQUEST_PLAY_GAME -> {
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
            R.id.menu_options_sync -> {
                WorkScheduler.enqueueManualWork(WorkSaveSync::class.java, this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayLemuroidHelp() {
        val systemFolders = ESystemType.values()
            .map { it.simpleName }
            .map { "<i>$it</i>" }
            .joinToString(", ")

        val message = getString(R.string.lemuroid_help_content).replace("\$SYSTEMS", systemFolders)
        AlertDialog.Builder(this)
            .setMessage(Html.fromHtml(message))
            .show()
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp()
}
