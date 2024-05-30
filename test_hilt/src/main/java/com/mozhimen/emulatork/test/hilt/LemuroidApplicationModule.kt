package com.mozhimen.emulatork.test.hilt

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mozhimen.emulatork.basic.EmulatorKBasic
import com.mozhimen.emulatork.basic.bios.BiosManager
import com.mozhimen.emulatork.basic.controller.ControllerConfigsManager
import com.mozhimen.emulatork.basic.core.CoreSelection
import com.mozhimen.emulatork.core.download.CoreDownload
import com.mozhimen.emulatork.core.download.CoreDownloaderImpl
import com.mozhimen.emulatork.core.variable.CoreVariableManager
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.game.db.daos.GameSearchDao
import com.mozhimen.emulatork.basic.game.db.helpers.Migrations
import com.mozhimen.emulatork.basic.metadata.MetadataProvider
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
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.libretro.db.database.LibretroDBManager
import com.mozhimen.emulatork.libretro.db.MetadataProviderLibretroDB
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
import javax.inject.Singleton

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
    @Singleton
    abstract fun saveSyncManager(saveSyncManagerImpl: SaveSyncManagerImpl): SaveSyncManager
}

@Module
@InstallIn(SingletonComponent::class)
class LemuroidApplicationModule2 {

    @Provides
    @Singleton
    fun gamePadPreferencesHelper(inputUnitManager: InputUnitManager) =
        GamePadPreferencesManager(inputUnitManager, GamePadBindingActivity::class.java, false)

    @Provides
    @Singleton
    fun libretroDBManager(@ApplicationContext context: Context): LibretroDBManager =
        LibretroDBManager(context)

    @Provides
    @Singleton
    fun retrogradeDb(@ApplicationContext context: Context): RetrogradeDatabase =
        Room.databaseBuilder(context, RetrogradeDatabase::class.java, RetrogradeDatabase.DB_NAME)
            .addCallback(GameSearchDao.CALLBACK)
            .addMigrations(GameSearchDao.MIGRATION, Migrations.VERSION_8_9)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun gameMetadataProvider(libretroDBManager: LibretroDBManager): MetadataProvider =
        MetadataProviderLibretroDB(libretroDBManager)

    @Provides
    @IntoSet
    @Singleton
    fun localSAFStorageProvider(@ApplicationContext context: Context): StorageProvider =
        StorageLocalAccessFrameworkProvider(context)

    @Provides
    @IntoSet
    @Singleton
    fun localGameStorageProvider(
        @ApplicationContext context: Context,
        storageProvider: StorageProvider
    ): StorageProvider =
        StorageLocalProvider(context, storageProvider)

    @Provides
    @Singleton
    fun gameStorageProviderRegistry(
        @ApplicationContext context: Context,
        providers: Set<@JvmSuppressWildcards StorageProvider>
    ) =
        StorageProviderRegistry(context, providers)

    @Provides
    @Singleton
    fun lemuroidLibrary(
        db: RetrogradeDatabase,
        storageProviderRegistry: Lazy<StorageProviderRegistry>,
        metadataProvider: Lazy<MetadataProvider>,
        biosManager: BiosManager
    ) = EmulatorKBasic(db, lazy { storageProviderRegistry.get() }, lazy { metadataProvider.get() }, biosManager)

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .build()

    @Provides
    @Singleton
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
    @Singleton
    fun directoriesManager(@ApplicationContext context: Context) = StorageProvider(context)

    @Provides
    @Singleton
    fun statesManager(storageProvider: StorageProvider) = SaveStateManager(storageProvider)

    @Provides
    @Singleton
    fun savesManager(storageProvider: StorageProvider) = SaveManager(storageProvider)

    @Provides
    @Singleton
    fun statesPreviewManager(storageProvider: StorageProvider) =
        SaveStatePreviewManager(storageProvider)

    @Provides
    @Singleton
    fun coreManager(
        storageProvider: StorageProvider,
        retrofit: Retrofit
    ): CoreDownload = CoreDownloaderImpl(storageProvider, retrofit)

    @Singleton
    @Provides
    fun coreVariablesManager(sharedPreferences: Lazy<SharedPreferences>) =
        CoreVariableManager(lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun gameLoader(
        lemuroidLibrary: EmulatorKBasic,
        saveStateManager: SaveStateManager,
        saveManager: SaveManager,
        coreVariableManager: CoreVariableManager,
        retrogradeDatabase: RetrogradeDatabase,
        saveCoherencyEngine: SaveCoherencyEngine,
        storageProvider: StorageProvider,
        biosManager: BiosManager
    ) = com.mozhimen.emulatork.common.game.GameLoader(
        lemuroidLibrary,
        saveStateManager,
        saveManager,
        coreVariableManager,
        retrogradeDatabase,
        saveCoherencyEngine,
        storageProvider,
        biosManager
    )

    @Singleton
    @Provides
    fun inputDeviceManager(@ApplicationContext context: Context, sharedPreferences: Lazy<SharedPreferences>) =
        InputUnitManager(context, lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun biosManager(storageProvider: StorageProvider) = BiosManager(storageProvider)

    @Singleton
    @Provides
    fun biosPreferences(biosManager: BiosManager) = PreferencesBios(biosManager)

    @Singleton
    @Provides
    fun coresSelection(sharedPreferences: Lazy<SharedPreferences>) =
        CoreSelection(lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun coreSelectionPreferences() = PreferencesCoreSelection()

    @Singleton
    @Provides
    fun savesCoherencyEngine(saveManager: SaveManager, saveStateManager: SaveStateManager) =
        SaveCoherencyEngine(saveManager, saveStateManager)

    @Singleton
    @Provides
    fun saveSyncManagerImpl(
        @ApplicationContext context: Context,
        storageProvider: StorageProvider
    ) = SaveSyncManagerImpl(context, storageProvider)

    @Singleton
    @Provides
    fun postGameHandler(retrogradeDatabase: RetrogradeDatabase) =
        GameLaunchTaskHandler(GameReviewManager(), retrogradeDatabase)

    @Singleton
    @Provides
    fun shortcutsGenerator(@ApplicationContext context: Context, retrofit: Retrofit) =
        CoverShortcutGenerator(context, retrofit)

    @Singleton
    @Provides
    fun retroControllerManager(sharedPreferences: Lazy<SharedPreferences>) =
        ControllerConfigsManager(lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun settingsManager(@ApplicationContext context: Context, sharedPreferences: Lazy<SharedPreferences>) =
        GameSettingsManager(context, lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun sharedPreferences(@ApplicationContext context: Context) =
        SharedPreferencesMgr.getSharedPreferences(context)

    @Singleton
    @Provides
    fun gameLauncher(
        coresSelection: CoreSelection,
        gameLaunchTaskHandler: GameLaunchTaskHandler
    ) =
        GameLauncher(coresSelection, gameLaunchTaskHandler)

    @Singleton
    @Provides
    fun rumbleManager(
        @ApplicationContext context: Context,
        gameSettingsManager: GameSettingsManager,
        inputUnitManager: InputUnitManager
    ) =
        GameRumbleManager(context, gameSettingsManager, inputUnitManager)

    @Singleton
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
    fun settingsInteractor(@ActivityContext context: Context, storageProvider: StorageProvider): SettingsInteractor =
        SettingsInteractor(context, storageProvider)
}
