package com.mozhimen.gamek.emulator.test.feature.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mozhimen.gamek.emulator.test.R
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeActivity()
    }

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