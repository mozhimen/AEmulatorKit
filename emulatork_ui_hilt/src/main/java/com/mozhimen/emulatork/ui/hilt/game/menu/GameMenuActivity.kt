package com.mozhimen.emulatork.ui.hilt.game.menu

import com.mozhimen.emulatork.ui.game.menu.AbsGameMenuActivity
import com.mozhimen.emulatork.ui.hilt.R

/**
 * @ClassName GameMenuActivity
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 21:50
 * @Version 1.0
 */
class GameMenuActivity : AbsGameMenuActivity() {

    override fun getGraphId(): Int {
        return R.navigation.mobile_game_menu
    }

//    @dagger.Module
//    abstract class Module {
//
//        @PerFragment
//        @ContributesAndroidInjector(modules = [GameMenuCoreOptionsFragment.Module::class])
//        abstract fun coreOptionsFragment(): GameMenuCoreOptionsFragment
//
//        @PerFragment
//        @ContributesAndroidInjector(modules = [GameMenuFragment.Module::class])
//        abstract fun gameMenuFragment(): GameMenuFragment
//
//        @PerFragment
//        @ContributesAndroidInjector(modules = [GameMenuLoadFragment.Module::class])
//        abstract fun gameMenuLoadFragment(): GameMenuLoadFragment
//
//        @PerFragment
//        @ContributesAndroidInjector(modules = [GameMenuSaveFragment.Module::class])
//        abstract fun gameMenuSaveFragment(): GameMenuSaveFragment
//    }
}