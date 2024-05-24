package com.mozhimen.emulatork.test.hilt

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mozhimen.emulatork.basic.EmulatorKBasic
import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.basic.controller.ControllerConfigsManager
import com.mozhimen.emulatork.basic.core.CoreSelection
import com.mozhimen.emulatork.basic.core.CoreUpdater
import com.mozhimen.emulatork.basic.core.CoreUpdaterImpl
import com.mozhimen.emulatork.basic.core.CoreVariablesManager
import com.mozhimen.emulatork.basic.game.GameLoader
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.game.db.daos.GameSearchDao
import com.mozhimen.emulatork.basic.game.db.helpers.Migrations
import com.mozhimen.emulatork.basic.game.metadata.GameMetadataProvider
import com.mozhimen.emulatork.basic.game.review.GameReviewManager
import com.mozhimen.emulatork.basic.game.rumble.GameRumbleManager
import com.mozhimen.emulatork.basic.game.setting.GameSettingsManager
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesMgr
import com.mozhimen.emulatork.basic.save.SaveCoherencyEngine
import com.mozhimen.emulatork.basic.save.SaveManager
import com.mozhimen.emulatork.basic.save.SaveStateManager
import com.mozhimen.emulatork.basic.save.SaveStatePreviewManager
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManager
import com.mozhimen.emulatork.basic.save.sync.SaveSyncManagerImpl
import com.mozhimen.emulatork.basic.storage.StorageDirectoriesManager
import com.mozhimen.emulatork.basic.storage.StorageProvider
import com.mozhimen.emulatork.basic.storage.StorageProviderRegistry
import com.mozhimen.emulatork.basic.storage.local.StorageLocalAccessFrameworkProvider
import com.mozhimen.emulatork.basic.storage.local.StorageLocalProvider
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.ext.covers.CoverShortcutGenerator
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.game.GameLaunchTaskHandler
import com.mozhimen.emulatork.ext.game.GameLauncher
import com.mozhimen.emulatork.ext.game.pad.GamePadPreferencesManager
import com.mozhimen.emulatork.ext.library.SettingsInteractor
import com.mozhimen.emulatork.ext.preferences.PreferencesBios
import com.mozhimen.emulatork.ext.preferences.PreferencesCoreSelection
import com.mozhimen.emulatork.input.device.InputDeviceManager
import com.mozhimen.emulatork.libretro.db.LibretroDBManager
import com.mozhimen.emulatork.libretro.db.LibretroDBMetadataProvider
import com.mozhimen.emulatork.ui.hilt.game.GameActivity
import com.mozhimen.emulatork.ui.hilt.game.pad.GamePadBindingActivity
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.InputStream
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

/**
 * @ClassName LemuroidApplicationModule
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LemuroidApplicationModule {
    @Binds
    abstract fun saveSyncManager(saveSyncManagerImpl: SaveSyncManagerImpl): SaveSyncManager
}

@Module
@InstallIn(SingletonComponent::class)
class LemuroidApplicationModule2 {

    @Provides
    fun gamePadPreferencesHelper(inputDeviceManager: InputDeviceManager) =
        GamePadPreferencesManager(inputDeviceManager, GamePadBindingActivity::class.java, false)

    @Provides
    fun libretroDBManager(@ApplicationContext context: Context): LibretroDBManager =
        LibretroDBManager(context)

    @Provides
    fun retrogradeDb(@ApplicationContext context: Context): RetrogradeDatabase =
        Room.databaseBuilder(context, RetrogradeDatabase::class.java, RetrogradeDatabase.DB_NAME)
            .addCallback(GameSearchDao.CALLBACK)
            .addMigrations(GameSearchDao.MIGRATION, Migrations.VERSION_8_9)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun gameMetadataProvider(libretroDBManager: LibretroDBManager): GameMetadataProvider =
        LibretroDBMetadataProvider(libretroDBManager)

    @Provides
    @IntoSet
    fun localSAFStorageProvider(@ApplicationContext context: Context): StorageProvider =
        StorageLocalAccessFrameworkProvider(context)

    @Provides
    @IntoSet
    fun localGameStorageProvider(
        @ApplicationContext context: Context,
        storageDirectoriesManager: StorageDirectoriesManager
    ): StorageProvider =
        StorageLocalProvider(context, storageDirectoriesManager)

    @Provides
    fun gameStorageProviderRegistry(
        @ApplicationContext context: Context,
        providers: Set<@JvmSuppressWildcards StorageProvider>
    ) =
        StorageProviderRegistry(context, providers)

    @Provides
    fun lemuroidLibrary(
        db: RetrogradeDatabase,
        storageProviderRegistry: Lazy<StorageProviderRegistry>,
        gameMetadataProvider: Lazy<GameMetadataProvider>,
        biosManager: BiosManager
    ) = EmulatorKBasic(db, lazy { storageProviderRegistry.get() }, lazy { gameMetadataProvider.get() }, biosManager)

    @Provides
    fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()

    @Provides
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
    fun directoriesManager(@ApplicationContext context: Context) = StorageDirectoriesManager(context)

    @Provides
    fun statesManager(storageDirectoriesManager: StorageDirectoriesManager) = SaveStateManager(storageDirectoriesManager)

    @Provides
    fun savesManager(storageDirectoriesManager: StorageDirectoriesManager) = SaveManager(storageDirectoriesManager)

    @Provides
    fun statesPreviewManager(storageDirectoriesManager: StorageDirectoriesManager) =
        SaveStatePreviewManager(storageDirectoriesManager)

    @Provides
    fun coreManager(
        storageDirectoriesManager: StorageDirectoriesManager,
        retrofit: Retrofit
    ): CoreUpdater = CoreUpdaterImpl(storageDirectoriesManager, retrofit)

    @Provides
    fun coreVariablesManager(sharedPreferences: Lazy<SharedPreferences>) =
        CoreVariablesManager(lazy { sharedPreferences.get() })

    @Provides
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
    fun inputDeviceManager(@ApplicationContext context: Context, sharedPreferences: Lazy<SharedPreferences>) =
        InputDeviceManager(context, lazy { sharedPreferences.get() })

    @Provides
    fun biosManager(storageDirectoriesManager: StorageDirectoriesManager) = BiosManager(storageDirectoriesManager)

    @Provides
    fun biosPreferences(biosManager: BiosManager) = PreferencesBios(biosManager)

    @Provides
    fun coresSelection(sharedPreferences: Lazy<SharedPreferences>) =
        CoreSelection(lazy { sharedPreferences.get() })

    @Provides
    fun coreSelectionPreferences() = PreferencesCoreSelection()

    @Provides
    fun savesCoherencyEngine(saveManager: SaveManager, saveStateManager: SaveStateManager) =
        SaveCoherencyEngine(saveManager, saveStateManager)

    @Provides
    fun saveSyncManagerImpl(
        @ApplicationContext context: Context,
        storageDirectoriesManager: StorageDirectoriesManager
    ) = SaveSyncManagerImpl(context, storageDirectoriesManager)

    @Provides
    fun postGameHandler(retrogradeDatabase: RetrogradeDatabase) =
        GameLaunchTaskHandler(GameReviewManager(), retrogradeDatabase)

    @Provides
    fun shortcutsGenerator(@ApplicationContext context: Context, retrofit: Retrofit) =
        CoverShortcutGenerator(context, retrofit)

    @Provides
    fun retroControllerManager(sharedPreferences: Lazy<SharedPreferences>) =
        ControllerConfigsManager(lazy { sharedPreferences.get() })

    @Provides
    fun settingsManager(@ApplicationContext context: Context, sharedPreferences: Lazy<SharedPreferences>) =
        GameSettingsManager(context, lazy { sharedPreferences.get() })

    @Provides
    fun sharedPreferences(@ApplicationContext context: Context) =
        SharedPreferencesMgr.getSharedPreferences(context)

    @Provides
    fun gameLauncher(
        coresSelection: CoreSelection,
        gameLaunchTaskHandler: GameLaunchTaskHandler
    ) =
        GameLauncher(coresSelection, gameLaunchTaskHandler)

    @Provides
    fun rumbleManager(
        @ApplicationContext context: Context,
        gameSettingsManager: GameSettingsManager,
        inputDeviceManager: InputDeviceManager
    ) =
        GameRumbleManager(context, gameSettingsManager, inputDeviceManager)

    @Provides
    fun coverLoader(
        @ApplicationContext context: Context
    ) = CoverLoader(context)
}

@Module
@InstallIn(ActivityComponent::class)
class LemuroidApplicationModule3 {
    @ActivityScoped
    @Provides
    fun gameInteractor(
        @ActivityContext activity: Context,
        retrogradeDb: RetrogradeDatabase,
        coverShortcutGenerator: CoverShortcutGenerator,
        gameLauncher: GameLauncher
    ) =
        GameInteractor(activity, GameActivity::class.java, retrogradeDb, false, coverShortcutGenerator, gameLauncher)

    @Provides
    @ActivityScoped
    fun settingsInteractor(@ActivityContext context: Context, storageDirectoriesManager: StorageDirectoriesManager): SettingsInteractor =
        SettingsInteractor(context, storageDirectoriesManager)
}
