package com.mozhimen.emulatork.test.feature.shortcuts

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.test.shared.covers.CoverLoader
import com.mozhimen.emulatork.test.shared.deeplink.DeepLink
import com.mozhimen.emulatork.util.bitmap.cropToSquare
import com.mozhimen.emulatork.util.bitmap.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.InputStream

/**
 * @ClassName ShortcutsGenerator
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class ShortcutsGenerator(
    private val appContext: Context,
    retrofit: Retrofit
) {

    private val thumbnailsApi = retrofit.create(ThumbnailsApi::class.java)

    suspend fun pinShortcutForGame(game: Game) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return

        val shortcutManager = appContext.getSystemService(ShortcutManager::class.java)!!
        val bitmap = retrieveBitmap(game)

        val shortcutInfo = ShortcutInfo.Builder(appContext, "game_${game.id}")
            .setShortLabel(game.title)
            .setLongLabel(game.title)
            .setIntent(DeepLink.launchIntentForGame(appContext, game))
            .setIcon(Icon.createWithBitmap(bitmap))
            .build()

        shortcutManager.requestPinShortcut(shortcutInfo, null)
    }

    private suspend fun retrieveBitmap(game: Game): Bitmap = withContext(Dispatchers.IO) {
        val result = runCatching {
            val response = thumbnailsApi.downloadThumbnail(game.coverFrontUrl!!)
            BitmapFactory.decodeStream(response.body()).cropToSquare()
        }
        result.getOrElse { retrieveFallbackBitmap(game) }
    }

    private fun retrieveFallbackBitmap(game: Game): Bitmap {
        val desiredIconSize = getDesiredIconSize()
        return CoverLoader.getFallbackDrawable(game).toBitmap(desiredIconSize, desiredIconSize)
    }

    private fun getDesiredIconSize(): Int {
        val am = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        return am?.launcherLargeIconSize ?: 256
    }

    fun supportShortcuts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return false

        val shortcutManager = appContext.getSystemService(ShortcutManager::class.java)!!
        return shortcutManager.isRequestPinShortcutSupported
    }

    interface ThumbnailsApi {
        @GET
        @Streaming
        suspend fun downloadThumbnail(@Url url: String): Response<InputStream>
    }
}
