package com.mozhimen.gamek.emulator.test

import android.content.Context
import android.preference.PreferenceManager
import androidx.room.Room
import com.mozhimen.gamek.emulator.basic.injection.PerActivity
import com.mozhimen.gamek.emulator.test.feature.main.MainActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoSet
import java.lang.reflect.Type
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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
abstract class LemuroidApplicationModule {

    @Binds
    abstract fun context(app: LemuroidApplication): Context

    @PerActivity
    @ContributesAndroidInjector(modules = [MainActivity.Module::class])
    abstract fun mainActivity(): MainActivity

    @PerActivity
    @ContributesAndroidInjector
    abstract fun gameLauncherActivity(): GameLauncherActivity

    @PerActivity
    @ContributesAndroidInjector
    abstract fun gameActivity(): GameActivity

    @Module
    companion object {
        @Provides
        @PerApp
        @JvmStatic
        fun executorService(): ExecutorService = Executors.newSingleThreadExecutor()

        @Provides
        @PerApp
        @JvmStatic
        fun ovgdbManager(app: LemuroidApplication, executorService: ExecutorService) =
            LibretroDBManager(app, executorService)

        @Provides
        @PerApp
        @JvmStatic
        fun retrogradeDb(app: LemuroidApplication) =
            Room.databaseBuilder(app, RetrogradeDatabase::class.java, RetrogradeDatabase.DB_NAME)
                .addCallback(GameSearchDao.CALLBACK)
                .addMigrations(GameSearchDao.MIGRATION)
                .fallbackToDestructiveMigration()
                .build()

        @Provides
        @PerApp
        @JvmStatic
        fun ovgdbMetadataProvider(ovgdbManager: LibretroDBManager) = LibretroDBMetadataProvider(ovgdbManager)

        @Provides
        @PerApp
        @IntoSet
        @JvmStatic
        fun localSAFStorageProvider(
            context: Context,
            metadataProvider: LibretroDBMetadataProvider,
            directoriesManager: DirectoriesManager
        ): StorageProvider =
            StorageAccessFrameworkProvider(context, metadataProvider, directoriesManager)

        @Provides
        @PerApp
        @IntoSet
        @JvmStatic
        fun localGameStorageProvider(context: Context, metadataProvider: LibretroDBMetadataProvider): StorageProvider =
            LocalStorageProvider(context, metadataProvider, true)

        @Provides
        @PerApp
        @JvmStatic
        fun gameStorageProviderRegistry(context: Context, providers: Set<@JvmSuppressWildcards StorageProvider>) =
            StorageProviderRegistry(context, providers)

        @Provides
        @PerApp
        @JvmStatic
        fun gameLibrary(
            db: RetrogradeDatabase,
            storageProviderRegistry: StorageProviderRegistry
        ) =
            GameLibrary(db, storageProviderRegistry)

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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .baseUrl("https://example.com")
            .addConverterFactory(object : Converter.Factory() {
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
                    return null
                }
            })
            .build()

        @Provides
        @PerApp
        @JvmStatic
        fun directoriesManager(context: Context) = DirectoriesManager(context)

        @Provides
        @PerApp
        @JvmStatic
        fun coreManager(directoriesManager: DirectoriesManager, retrofit: Retrofit) = CoreManager(directoriesManager, retrofit)

        @Provides
        @PerApp
        @JvmStatic
        fun rxTree() = RxTimberTree()

        @Provides
        @PerApp
        @JvmStatic
        fun rxPrefs(context: Context) =
            RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(context))

        @Provides
        @PerApp
        @JvmStatic
        fun gameLoader(coreManager: CoreManager, retrogradeDatabase: RetrogradeDatabase, gameLibrary: GameLibrary) =
            GameLoader(coreManager, retrogradeDatabase, gameLibrary)
    }
}
