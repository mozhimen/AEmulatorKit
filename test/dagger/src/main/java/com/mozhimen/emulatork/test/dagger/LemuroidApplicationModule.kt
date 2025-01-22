package com.mozhimen.emulatork.test.dagger

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mozhimen.emulatork.core.download.CoreDownload
import com.mozhimen.emulatork.common.dagger.annors.PerActivity
import com.mozhimen.emulatork.common.dagger.annors.PerApp
import com.mozhimen.emulatork.basic.metadata.MetadataProvider
import com.mozhimen.emulatork.basic.preferences.SharedPreferencesManager
import com.mozhimen.emulatork.basic.setting.SettingManager
import com.mozhimen.emulatork.basic.storage.StorageDirProvider
import com.mozhimen.emulatork.common.EmulatorKCommon
import com.mozhimen.emulatork.common.archive.ArchiveCoherencyEngine
import com.mozhimen.emulatork.ext.preferences.PreferencesCoreSelection
import com.mozhimen.emulatork.ext.game.GameLaunchTaskHandler
import com.mozhimen.emulatork.ext.covers.CoverShortcutGenerator
import com.mozhimen.emulatork.ext.game.GameLauncher
import com.mozhimen.emulatork.ext.covers.CoverLoader
import com.mozhimen.emulatork.core.download.CoreDownloaderImpl
import com.mozhimen.emulatork.common.metadata.MetadataProviderLibretroDB
import com.mozhimen.emulatork.db.libretro.database.LibretroDBManager
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
import com.mozhimen.emulatork.common.core.CorePropertyManager
import com.mozhimen.emulatork.common.archive.ArchiveManager
import com.mozhimen.emulatork.common.archive.ArchiveManagerImpl
import com.mozhimen.emulatork.common.bios.BiosManager
import com.mozhimen.emulatork.common.core.CoreSelectionManager
import com.mozhimen.emulatork.common.input.GamepadConfigManager
import com.mozhimen.emulatork.common.storage.StorageProvider
import com.mozhimen.emulatork.common.storage.StorageProviderAccessFramework
import com.mozhimen.emulatork.common.storage.StorageProviderLocal
import com.mozhimen.emulatork.common.storage.StorageProviderRegistry
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.db.game.helpers.Migrations
import com.mozhimen.emulatork.common.save.SaveManager
import com.mozhimen.emulatork.common.save.SaveStateManager
import com.mozhimen.emulatork.common.save.SaveStatePreviewManager
import com.mozhimen.emulatork.common.input.RumbleManager

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
    abstract fun saveSyncManager(saveSyncManagerImpl: ArchiveManagerImpl): ArchiveManager

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
        fun libretroDBManager(app: LemuroidApplication) = LibretroDBManager()

        @Provides
        @PerApp
        @JvmStatic
        fun retrogradeDb(app: LemuroidApplication) =
            Room.databaseBuilder(app, RetrogradeDatabase::class.java, RetrogradeDatabase.DB_NAME)
                .addCallback(Migrations.CALLBACK)
                .addMigrations(Migrations.VERSION_7_8, Migrations.VERSION_8_9)
                .fallbackToDestructiveMigration()
                .build()

        @Provides
        @PerApp
        @JvmStatic
        fun gameMetadataProvider(libretroDBManager: LibretroDBManager): MetadataProvider =
            MetadataProviderLibretroDB(libretroDBManager)

        @Provides
        @PerApp
        @IntoSet
        @JvmStatic
        fun localSAFStorageProvider(context: Context): StorageProvider =
            StorageProviderAccessFramework(context)

        @Provides
        @PerApp
        @IntoSet
        @JvmStatic
        fun localGameStorageProvider(
            context: Context,
            storageProvider: StorageDirProvider
        ): StorageProvider =
            StorageProviderLocal(context, storageProvider)

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
            metadataProvider: Lazy<MetadataProvider>,
            biosManager: BiosManager
        ) = EmulatorKCommon(db, lazy { storageProviderRegistry.get() }, lazy { metadataProvider.get() }, biosManager)

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
        fun storageDirProvider(context: Context) =
            StorageDirProvider(context)

        @Provides
        @PerApp
        @JvmStatic
        fun statesManager(storageDirProvider: StorageDirProvider) =
            SaveStateManager(storageDirProvider)

        @Provides
        @PerApp
        @JvmStatic
        fun savesManager(storageDirProvider: StorageDirProvider) =
            SaveManager(storageDirProvider)

        @Provides
        @PerApp
        @JvmStatic
        fun statesPreviewManager(storageProvider: StorageDirProvider) =
            SaveStatePreviewManager(storageProvider)

        @Provides
        @PerApp
        @JvmStatic
        fun coreManager(
            storageProvider: StorageDirProvider,
            retrofit: Retrofit
        ): CoreDownload = CoreDownloaderImpl(storageProvider, retrofit)

        @Provides
        @PerApp
        @JvmStatic
        fun coreVariablesManager(sharedPreferences: Lazy<SharedPreferences>) =
            CorePropertyManager(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
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

        @Provides
        @PerApp
        @JvmStatic
        fun inputDeviceManager(context: Context, sharedPreferences: Lazy<SharedPreferences>) =
            InputUnitManager(context, lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun biosManager(storageProvider: StorageDirProvider) = BiosManager(storageProvider)

        @Provides
        @PerApp
        @JvmStatic
        fun biosPreferences(biosManager: BiosManager) = PreferencesBios(biosManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coreSelectionManager(sharedPreferences: Lazy<SharedPreferences>) =
            CoreSelectionManager(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun coreSelectionPreferences() = PreferencesCoreSelection()

        @Provides
        @PerApp
        @JvmStatic
        fun archiveCoherencyEngine(saveManager: SaveManager, saveStateManager: SaveStateManager) =
            ArchiveCoherencyEngine(saveManager, saveStateManager)

        @Provides
        @PerApp
        @JvmStatic
        fun archiveManagerImpl(
            context: Context,
            storageProvider: StorageDirProvider
        ) = ArchiveManagerImpl(context, storageProvider)

        @Provides
        @PerApp
        @JvmStatic
        fun postGameHandler(retrogradeDatabase: RetrogradeDatabase) =
            GameLaunchTaskHandler(/*GameReviewManager(),*/ retrogradeDatabase)

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
        fun gamepadConfigManager(sharedPreferences: Lazy<SharedPreferences>) =
            GamepadConfigManager(lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun settingManager(context: Context, sharedPreferences: Lazy<SharedPreferences>) =
            SettingManager(context, lazy { sharedPreferences.get() })

        @Provides
        @PerApp
        @JvmStatic
        fun sharedPreferences(context: Context) =
            SharedPreferencesManager.getSharedPreferences(context)

        @Provides
        @PerApp
        @JvmStatic
        fun gameLauncher(
            coreSelectionManager: CoreSelectionManager,
            gameLaunchTaskHandler: GameLaunchTaskHandler
        ) =
            GameLauncher(coreSelectionManager, gameLaunchTaskHandler)

        @Provides
        @PerApp
        @JvmStatic
        fun rumbleManager(
            context: Context,
            settingsManager: SettingManager,
            inputUnitManager: InputUnitManager
        ) =
            RumbleManager(context, settingsManager, inputUnitManager)

        @Provides
        @PerApp
        @JvmStatic
        fun coverLoader(
            context: Context
        ) = CoverLoader(context)
    }
}
