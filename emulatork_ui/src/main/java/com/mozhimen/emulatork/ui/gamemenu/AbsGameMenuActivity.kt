package com.mozhimen.emulatork.ui.gamemenu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.mozhimen.emulatork.ui.R

/**
 * @ClassName GameMenuActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
abstract class AbsGameMenuActivity : AppCompatActivity() {

    abstract fun getGraphId():Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_navigation_overlay)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(getGraphId()/*R.navigation.mobile_game_menu*/, intent.extras)

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
