package com.mozhimen.emulatork.basic.storage

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.mozhimen.emulatork.basic.library.db.entities.Game

/**
 * @ClassName StorageProviderRegistry
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class StorageProviderRegistry(context: Context, val providers: Set<StorageProvider>) {

    companion object {
        const val PREF_NAME = "storage_providers"
    }

    private val providersByScheme = mapOf(
        *providers.map { provider ->
            provider.uriSchemes.map { scheme -> scheme to provider }
        }.flatten().toTypedArray()
    )

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    val enabledProviders: Iterable<StorageProvider>
        get() = providers.filter { prefs.getBoolean(it.id, it.enabledByDefault) }

    fun getProvider(game: Game): StorageProvider {
        val uri = Uri.parse(game.fileUri)
        return providersByScheme[uri.scheme]!!
    }
}
