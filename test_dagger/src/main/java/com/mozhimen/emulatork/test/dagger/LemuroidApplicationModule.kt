package com.mozhimen.emulatork.test.dagger

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.dagger.annors.PerActivity
import com.mozhimen.emulatork.basic.dagger.annors.PerApp
import com.mozhimen.emulatork.basic.game.GameLoader
import com.mozhimen.emulatork.basic.EmulatorKBasic
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.game.db.daos.GameSearchDao
import com.mozhimen.emulatork.basic.game.db.helpers.Migrations
import com.mozhimen.emulatork.basic.game.metadata.GameMetadataProvider
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesMgr
import com.mozhimen.emulatork.basic.save.SaveCoherencyEngine
import com.mozhimen.emulatork.basic.save.SaveManager
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.save.SaveStatePreviewManager
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManager
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.basic.storage.StorageProviderRegistry
import com.mozhimen.emulatork.basic.storage.local.StorageLocalProvider
import com.mozhimen.emulatork.basic.storage.local.StorageLocalAccessFrameworkProvider
import com.mozhimen.emulatork.ext.preferences.PreferencesCoreSelection
import com.mozhimen.emulatork.ext.game.GameLaunchTaskHandler
import com.mozhimen.emulatork.ext.covers.CoverShortcutGenerator
import com.mozhimen.emulatork.ext.game.GameLauncher
import com.mozhimen.emulatork.basic.game.rumble.GameRumbleManager
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.basic.core.CoreUpdaterImpl
import com.mozhimen.emulatork.basic.game.review.GameReviewManager
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManagerImpl
import com.mozhimen.emulatork.libretro.db.LibretroDBMetadataProvider
import com.mozhimen.emulatork.libretro.db.database.LibretroDBManager
import com.mozhimen.emulatork.basic.controller.ControllerConfigsManager
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
import com.mozhimen.emulatork.test.dagger.main.MainActivity
import com.mozhimen.emulatork.ui.dagger.game.ExternalGameLauncherActivity
import com.mozhimen.emulatork.ui.dagger.game.GameActivity
import com.mozhimen.emulatork.ui.dagger.game.menu.GameMenuActivity
import com.mozhimen.emulatork.ui.dagger.game.pad.GamePadBindingActivity
import com.mozhimen.emulatork.ui.dagger.settings.StorageFrameworkPickerActivity
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.ext.preferences.PreferencesBios
import com.mozhimen.emulatork.basic.game.setting.GameSettingsManager
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import com.mozhimen.emulatork.basic.core.CoreSelection

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
    abstract fun storageFrameworkPickerLauncher(): StorageFrameworkPickerActivity

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
            StorageLocalAccessFrameworkProvider(context)

        @Provides
        @PerApp
        @IntoSet
        @JvmStatic
        fun localGameStorageProvider(
            context: Context,
            storageDirectoriesManager: StorageDirectoriesManager
        ): StorageProvider =
            StorageLocalProvider(context, storageDirectoriesManager)

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
        ) = EmulatorKBasic(db, lazy { storageProviderRegistry.get() }, lazy { gameMetadataProvider.get() }, biosManager)

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
        fun directoriesManager(context: Context) = StorageDirectoriesManager(context)

        @Provides
        @PerApp
        @JvmStatic
        fun statesManager(storageDirectoriesManager: StorageDirectoriesManager) = SaveStateManager(storageDirectoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun savesManager(storageDirectoriesManager: StorageDirectoriesManager) = SaveManager(storageDirectoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun statesPreviewManager(storageDirectoriesManager: StorageDirectoriesManager) =
            SaveStatePreviewManager(storageDirectoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coreManager(
            storageDirectoriesManager: StorageDirectoriesManager,
            retrofit: Retrofit
        ): CoreUpdater = CoreUpdaterImpl(storageDirectoriesManager, retrofit)

        @Provides
        @PerApp
        @JvmStatic
        fun coreVariablesManager(sharedPreferences: Lazy<SharedPreferences>) =
            CoreVariablesManager(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun gameLoader(
            lemuroidLibrary: EmulatorKBasic,
            saveStateManager: SaveStateManager,
            saveManager: SaveManager,
            coreVariablesManager: CoreVariablesManager,
            retrogradeDatabase: RetrogradeDatabase,
            saveCoherencyEngine: SaveCoherencyEngine,
            storageDirectoriesManager: StorageDirectoriesManager,
            biosManager: BiosManager
        ) = GameLoader(
            lemuroidLibrary,
            saveStateManager,
            saveManager,
            coreVariablesManager,
            retrogradeDatabase,
            saveCoherencyEngine,
            storageDirectoriesManager,
            biosManager
        )

        @Provides
        @PerApp
        @JvmStatic
        fun inputDeviceManager(context: Context, sharedPreferences: Lazy<SharedPreferences>) =
            InputUnitManager(context, lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun biosManager(storageDirectoriesManager: StorageDirectoriesManager) = BiosManager(storageDirectoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun biosPreferences(biosManager: BiosManager) = PreferencesBios(biosManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coresSelection(sharedPreferences: Lazy<SharedPreferences>) =
            CoreSelection(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun coreSelectionPreferences() = PreferencesCoreSelection()

        @Provides
        @PerApp
        @JvmStatic
        fun savesCoherencyEngine(saveManager: SaveManager, saveStateManager: SaveStateManager) =
            SaveCoherencyEngine(saveManager, saveStateManager)

        @Provides
        @PerApp
        @JvmStatic
        fun saveSyncManagerImpl(
            context: Context,
            storageDirectoriesManager: StorageDirectoriesManager
        ) = SaveSyncManagerImpl(context, storageDirectoriesManager)

        @Provides
        @PerApp
        @JvmStatic
        fun postGameHandler(retrogradeDatabase: RetrogradeDatabase) =
            GameLaunchTaskHandler(GameReviewManager(), retrogradeDatabase)

        @Provides
        @PerApp
        @JvmStatic
        fun shortcutsGenerator(context: Context, retrofit: Retrofit) =
            CoverShortcutGenerator(context, retrofit)

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
            GameSettingsManager(context, lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun sharedPreferences(context: Context) =
            SharedPreferencesMgr.getSharedPreferences(context)

        @Provides
        @PerApp
        @JvmStatic
        fun gameLauncher(
            coresSelection: CoreSelection,
            gameLaunchTaskHandler: GameLaunchTaskHandler
        ) =
            GameLauncher(coresSelection, gameLaunchTaskHandler)

        @Provides
        @PerApp
        @JvmStatic
        fun rumbleManager(
            context: Context,
            gameSettingsManager: GameSettingsManager,
            inputUnitManager: InputUnitManager
        ) =
            GameRumbleManager(context, gameSettingsManager, inputUnitManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coverLoader(
            context: Context
        ) = CoverLoader(context)
    }
}
