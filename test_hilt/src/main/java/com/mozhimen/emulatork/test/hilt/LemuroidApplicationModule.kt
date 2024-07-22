package com.mozhimen.emulatork.test.hilt

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mozhimen.emulatork.core.download.CoreDownload
import com.mozhimen.emulatork.core.download.CoreDownloaderImpl
import com.mozhimen.emulatork.common.core.CorePropertyManager
import com.mozhimen.emulatork.basic.metadata.MetadataProvider
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.common.EmulatorKCommon
import com.mozhimen.emulatork.common.archive.ArchiveCoherencyEngine
import com.mozhimen.emulatork.common.archive.ArchiveManager
import com.mozhimen.emulatork.common.archive.ArchiveManagerImpl
import com.mozhimen.emulatork.common.bios.BiosManager
import com.mozhimen.emulatork.common.core.CoreSelectionManager
import com.mozhimen.emulatork.common.input.GamepadConfigManager
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.ext.covers.CoverShortcutGenerator
import com.mozhimen.emulatork.ext.game.GameInteractor
import com.mozhimen.emulatork.ext.game.GameLaunchTaskHandler
import com.mozhimen.emulatork.ext.game.GameLauncher
import com.mozhimen.emulatork.ext.input.GamePadPreferencesManager
import com.mozhimen.emulatork.ext.library.SettingsInteractor
import com.mozhimen.emulatork.ext.preferences.PreferencesBios
import com.mozhimen.emulatork.ext.preferences.PreferencesCoreSelection
import com.mozhimen.emulatork.input.unit.InputUnitManager
import com.mozhimen.emulatork.db.libretro.database.LibretroDBManager
import com.mozhimen.emulatork.common.metadata.MetadataProviderLibretroDB
import com.mozhimen.emulatork.common.storage.StorageProvider
import com.mozhimen.emulatork.common.storage.StorageProviderAccessFramework
import com.mozhimen.emulatork.common.storage.StorageProviderLocal
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
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
import com.mozhimen.emulatork.db.game.helpers.Migrations
import com.mozhimen.emulatork.common.storage.StorageProviderRegistry
import com.mozhimen.emulatork.common.save.SaveStateManager
import com.mozhimen.emulatork.common.save.SaveManager
import com.mozhimen.emulatork.common.save.SaveStatePreviewManager
import  com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.common.input.RumbleManager

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
    abstract fun saveSyncManager(saveSyncManagerImpl: ArchiveManagerImpl): ArchiveManager
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
        LibretroDBManager()

    @Provides
    @Singleton
    fun retrogradeDb(@ApplicationContext context: Context): RetrogradeDatabase =
        Room.databaseBuilder(context, RetrogradeDatabase::class.java, RetrogradeDatabase.DB_NAME)
            .addCallback(Migrations.CALLBACK)
            .addMigrations(Migrations.VERSION_7_8, Migrations.VERSION_8_9)
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
        StorageProviderAccessFramework(context)

    @Provides
    @IntoSet
    @Singleton
    fun localGameStorageProvider(
        @ApplicationContext context: Context,
        storageDirProvider: StorageDirProvider
    ): StorageProvider =
        StorageProviderLocal(context, storageDirProvider)

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
    ) = EmulatorKCommon(db, lazy { storageProviderRegistry.get() }, lazy { metadataProvider.get() }, biosManager)

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
    fun directoriesManager(@ApplicationContext context: Context) = StorageDirProvider(context)

    @Provides
    @Singleton
    fun statesManager(storageProvider: StorageDirProvider) = SaveStateManager(storageProvider)

    @Provides
    @Singleton
    fun savesManager(storageProvider: StorageDirProvider) = SaveManager(storageProvider)

    @Provides
    @Singleton
    fun statesPreviewManager(storageProvider: StorageDirProvider) =
        SaveStatePreviewManager(storageProvider)

    @Provides
    @Singleton
    fun coreManager(
        storageProvider: StorageDirProvider,
        retrofit: Retrofit
    ): CoreDownload = CoreDownloaderImpl(storageProvider, retrofit)

    @Singleton
    @Provides
    fun coreVariablesManager(sharedPreferences: Lazy<SharedPreferences>) =
        CorePropertyManager(lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun gameLoader(
        emulatorKCommon: EmulatorKCommon,
        saveStateManager: SaveStateManager,
        saveManager: SaveManager,
        corePropertyManager: CorePropertyManager,
        retrogradeDatabase: RetrogradeDatabase,
        archiveCoherencyEngine: ArchiveCoherencyEngine,
        storageProvider: StorageDirProvider,
        biosManager: BiosManager
    ) = com.mozhimen.emulatork.common.game.GameLoadManager(
        emulatorKCommon,
        saveStateManager,
        saveManager,
        corePropertyManager,
        retrogradeDatabase,
        archiveCoherencyEngine,
        storageProvider,
        biosManager
    )

    @Singleton
    @Provides
    fun inputDeviceManager(@ApplicationContext context: Context, sharedPreferences: Lazy<SharedPreferences>) =
        InputUnitManager(context, lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun biosManager(storageProvider: StorageDirProvider) = BiosManager(storageProvider)

    @Singleton
    @Provides
    fun biosPreferences(biosManager: BiosManager) = PreferencesBios(biosManager)

    @Singleton
    @Provides
    fun coresSelectionManager(sharedPreferences: Lazy<SharedPreferences>) =
        CoreSelectionManager(lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun coreSelectionPreferences() = PreferencesCoreSelection()

    @Singleton
    @Provides
    fun archiveCoherencyEngine(saveManager: SaveManager, saveStateManager: SaveStateManager) =
        ArchiveCoherencyEngine(saveManager, saveStateManager)

    @Singleton
    @Provides
    fun archiveManagerImpl(
        @ApplicationContext context: Context,
        storageProvider: StorageDirProvider
    ) = ArchiveManagerImpl(context, storageProvider)

    @Singleton
    @Provides
    fun postGameHandler(retrogradeDatabase: RetrogradeDatabase) =
        GameLaunchTaskHandler(/*GameReviewManager(), */retrogradeDatabase)

    @Singleton
    @Provides
    fun shortcutsGenerator(@ApplicationContext context: Context, retrofit: Retrofit) =
        CoverShortcutGenerator(context, retrofit)

    @Singleton
    @Provides
    fun gamepadConfigManager(sharedPreferences: Lazy<SharedPreferences>) =
        GamepadConfigManager(lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun settingsManager(@ApplicationContext context: Context, sharedPreferences: Lazy<SharedPreferences>) =
        SettingManager(context, lazy { sharedPreferences.get() })

    @Singleton
    @Provides
    fun sharedPreferences(@ApplicationContext context: Context) =
        SharedPreferencesManager.getSharedPreferences(context)

    @Singleton
    @Provides
    fun gameLauncher(
        coreSelectionManager: CoreSelectionManager,
        gameLaunchTaskHandler: GameLaunchTaskHandler
    ) =
        GameLauncher(coreSelectionManager, gameLaunchTaskHandler)

    @Singleton
    @Provides
    fun rumbleManager(
        @ApplicationContext context: Context,
        settingManager: SettingManager,
        inputUnitManager: InputUnitManager
    ) =
        RumbleManager(context, settingManager, inputUnitManager)

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
    fun settingsInteractor(@ActivityContext context: Context, storageProvider: StorageDirProvider): SettingsInteractor =
        SettingsInteractor(context, storageProvider)
}
