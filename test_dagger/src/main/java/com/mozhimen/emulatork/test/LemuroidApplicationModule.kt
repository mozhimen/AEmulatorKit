package com.mozhimen.emulatork.test

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.dagger.interfaces.PerActivity
import com.mozhimen.emulatork.basic.dagger.interfaces.PerApp
import com.mozhimen.emulatork.basic.game.GameLoader
import com.mozhimen.emulatork.basic.library.LemuroidLibrary
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.library.db.daos.GameSearchDao
import com.mozhimen.emulatork.basic.library.db.helpers.Migrations
import com.mozhimen.emulatork.basic.library.metadata.GameMetadataProvider
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesHelper
import com.mozhimen.emulatork.basic.saves.SavesCoherencyEngine
import com.mozhimen.emulatork.basic.saves.SavesManager
import com.mozhimen.emulatork.basic.saves.StatesManager
import com.mozhimen.emulatork.basic.saves.StatesPreviewManager
import com.mozhimen.emulatork.basic.savesync.SaveSyncManager
import com.mozhimen.emulatork.basic.storage.DirectoriesManager
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.basic.storage.StorageProviderRegistry
import com.mozhimen.emulatork.basic.storage.local.LocalStorageProvider
import com.mozhimen.emulatork.basic.storage.local.StorageAccessFrameworkProvider
import com.mozhimen.emulatork.ui.settings.CoresSelectionPreferences
import com.mozhimen.emulatork.ui.main.GameLaunchTaskHandler
import com.mozhimen.emulatork.ui.shortcuts.ShortcutsGenerator
import com.mozhimen.emulatork.ui.game.GameLauncher
import com.mozhimen.emulatork.ui.rumble.RumbleManager
import com.mozhimen.emulatork.ui.covers.CoverLoader
import com.mozhimen.emulatork.ext.core.CoreUpdaterImpl
import com.mozhimen.emulatork.ext.review.ReviewManager
import com.mozhimen.emulatork.ext.savesync.SaveSyncManagerImpl
import com.mozhimen.emulatork.libretro.LibretroDBMetadataProvider
import com.mozhimen.emulatork.libretro.db.LibretroDBManager
import com.mozhimen.emulatork.ui.settings.ControllerConfigsManager
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.InputStream
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import com.mozhimen.emulatork.test.main.MainActivity
import com.mozhimen.emulatork.ui.dagger.game.ExternalGameLauncherActivity
import com.mozhimen.emulatork.ui.dagger.game.GameActivity
import com.mozhimen.emulatork.ui.dagger.gamemenu.GameMenuActivity
import com.mozhimen.emulatork.ui.dagger.input.GamePadBindingActivity
import com.mozhimen.emulatork.ui.dagger.settings.StorageFrameworkPickerLauncher
import com.mozhimen.emulatork.ui.input.InputDeviceManager
import com.mozhimen.emulatork.ui.settings.BiosPreferences
import com.mozhimen.emulatork.ui.settings.SettingsManager
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import com.mozhimen.emulatork.basic.core.CoresSelection

/**
 * @ClassName LemuroidApplicationModule
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
@Module
abstract class LemuroidApplicationModule {

    @Binds
    abstract fun context(app: LemuroidApplication): Context

    @Binds
    abstract fun saveSyncManager(saveSyncManagerImpl: SaveSyncManagerImpl): SaveSyncManager

    @PerActivity
    @ContributesAndroidInjector(modules = [MainActivity.Module::class])
    abstract fun mainActivity(): MainActivity

    @PerActivity
    @ContributesAndroidInjector
    abstract fun externalGameLauncherActivity(): ExternalGameLauncherActivity

    @PerActivity
    @ContributesAndroidInjector
    abstract fun gameActivity(): GameActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [GameMenuActivity.Module::class])
    abstract fun gameMenuActivity(): GameMenuActivity

    @PerActivity
    @ContributesAndroidInjector
    abstract fun storageFrameworkPickerLauncher(): StorageFrameworkPickerLauncher

    @PerActivity
    @ContributesAndroidInjector(modules = [GamePadBindingActivity.Module::class])
    abstract fun gamepadBindingActivity(): GamePadBindingActivity

    @Module
    companion object {
        @Provides
        @PerApp
        @JvmStatic
        fun libretroDBManager(app: LemuroidApplication) = LibretroDBManager(app)

        @Provides
        @PerApp
        @JvmStatic
        fun retrogradeDb(app: LemuroidApplication) =
            Room.databaseBuilder(app, RetrogradeDatabase::class.java, RetrogradeDatabase.DB_NAME)
                .addCallback(GameSearchDao.CALLBACK)
                .addMigrations(GameSearchDao.MIGRATION, Migrations.VERSION_8_9)
                .fallbackToDestructiveMigration()
                .build()

        @Provides
        @PerApp
        @JvmStatic
        fun gameMetadataProvider(libretroDBManager: LibretroDBManager): GameMetadataProvider =
            LibretroDBMetadataProvider(libretroDBManager)

        @Provides
        @PerApp
        @IntoSet
        @JvmStatic
        fun localSAFStorageProvider(context: Context): StorageProvider =
            StorageAccessFrameworkProvider(context)

        @Provides
        @PerApp
        @IntoSet
        @JvmStatic
        fun localGameStorageProvider(
            context: Context,
            directoriesManager: DirectoriesManager
        ): StorageProvider =
            LocalStorageProvider(context, directoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun gameStorageProviderRegistry(
            context: Context,
            providers: Set<@JvmSuppressWildcards StorageProvider>
        ) =
            StorageProviderRegistry(context, providers)

        @Provides
        @PerApp
        @JvmStatic
        fun lemuroidLibrary(
            db: RetrogradeDatabase,
            storageProviderRegistry: Lazy<StorageProviderRegistry>,
            gameMetadataProvider: Lazy<GameMetadataProvider>,
            biosManager: BiosManager
        ) = LemuroidLibrary(db, lazy { storageProviderRegistry.get() }, lazy { gameMetadataProvider.get() }, biosManager)

        @Provides
        @PerApp
        @JvmStatic
        fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        @Provides
        @PerApp
        @JvmStatic
        fun retrofit(): Retrofit = Retrofit.Builder()
            .baseUrl("https://example.com")
            .addConverterFactory(
                object : Converter.Factory() {
                    override fun responseBodyConverter(
                        type: Type?,
                        annotations: Array<out Annotation>?,
                        retrofit: Retrofit?
                    ): Converter<ResponseBody, *>? {
                        if (type == ZipInputStream::class.java) {
                            return Converter<ResponseBody, ZipInputStream> { responseBody ->
                                ZipInputStream(responseBody.byteStream())
                            }
                        }
                        if (type == InputStream::class.java) {
                            return Converter<ResponseBody, InputStream> { responseBody ->
                                responseBody.byteStream()
                            }
                        }
                        return null
                    }
                }
            )
            .build()

        @Provides
        @PerApp
        @JvmStatic
        fun directoriesManager(context: Context) = DirectoriesManager(context)

        @Provides
        @PerApp
        @JvmStatic
        fun statesManager(directoriesManager: DirectoriesManager) = StatesManager(directoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun savesManager(directoriesManager: DirectoriesManager) = SavesManager(directoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun statesPreviewManager(directoriesManager: DirectoriesManager) =
            StatesPreviewManager(directoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coreManager(
            directoriesManager: DirectoriesManager,
            retrofit: Retrofit
        ): CoreUpdater = CoreUpdaterImpl(directoriesManager, retrofit)

        @Provides
        @PerApp
        @JvmStatic
        fun coreVariablesManager(sharedPreferences: Lazy<SharedPreferences>) =
            CoreVariablesManager(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun gameLoader(
            lemuroidLibrary: LemuroidLibrary,
            statesManager: StatesManager,
            savesManager: SavesManager,
            coreVariablesManager: CoreVariablesManager,
            retrogradeDatabase: RetrogradeDatabase,
            savesCoherencyEngine: SavesCoherencyEngine,
            directoriesManager: DirectoriesManager,
            biosManager: BiosManager
        ) = GameLoader(
            lemuroidLibrary,
            statesManager,
            savesManager,
            coreVariablesManager,
            retrogradeDatabase,
            savesCoherencyEngine,
            directoriesManager,
            biosManager
        )

        @Provides
        @PerApp
        @JvmStatic
        fun inputDeviceManager(context: Context, sharedPreferences: Lazy<SharedPreferences>) =
            InputDeviceManager(context, lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun biosManager(directoriesManager: DirectoriesManager) = BiosManager(directoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun biosPreferences(biosManager: BiosManager) = BiosPreferences(biosManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coresSelection(sharedPreferences: Lazy<SharedPreferences>) =
            CoresSelection(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun coreSelectionPreferences() = CoresSelectionPreferences()

        @Provides
        @PerApp
        @JvmStatic
        fun savesCoherencyEngine(savesManager: SavesManager, statesManager: StatesManager) =
            SavesCoherencyEngine(savesManager, statesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun saveSyncManagerImpl(
            context: Context,
            directoriesManager: DirectoriesManager
        ) = SaveSyncManagerImpl(context, directoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun postGameHandler(retrogradeDatabase: RetrogradeDatabase) =
            GameLaunchTaskHandler(ReviewManager(), retrogradeDatabase)

        @Provides
        @PerApp
        @JvmStatic
        fun shortcutsGenerator(context: Context, retrofit: Retrofit) =
            ShortcutsGenerator(context, retrofit)

//        @Provides
//        @PerApp
//        @JvmStatic
//        fun channelHandler(
//            context: Context,
//            retrogradeDatabase: RetrogradeDatabase,
//            retrofit: Retrofit
//        ) =
//            ChannelHandler(context, retrogradeDatabase, retrofit)

        @Provides
        @PerApp
        @JvmStatic
        fun retroControllerManager(sharedPreferences: Lazy<SharedPreferences>) =
            ControllerConfigsManager(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun settingsManager(context: Context, sharedPreferences: Lazy<SharedPreferences>) =
            SettingsManager(context, lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun sharedPreferences(context: Context) =
            SharedPreferencesHelper.getSharedPreferences(context)

        @Provides
        @PerApp
        @JvmStatic
        fun gameLauncher(
            coresSelection: CoresSelection,
            gameLaunchTaskHandler: GameLaunchTaskHandler
        ) =
            GameLauncher(coresSelection, gameLaunchTaskHandler)

        @Provides
        @PerApp
        @JvmStatic
        fun rumbleManager(
            context: Context,
            settingsManager: SettingsManager,
            inputDeviceManager: InputDeviceManager
        ) =
            RumbleManager(context, settingsManager, inputDeviceManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coverLoader(
            context: Context
        ) = CoverLoader(context)
    }
}
