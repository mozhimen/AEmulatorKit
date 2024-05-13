package com.mozhimen.emulatork.test.shared.covers

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.util.CoilUtils
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.util.drawable.TextDrawable
import com.mozhimen.emulatork.util.graphics.ColorUtils
import okhttp3.OkHttpClient

/**
 * @ClassName CoverLoader
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
class CoverLoader(applicationContext: Context) {

    private val imageLoader = ImageLoader.Builder(applicationContext)
        .crossfade(true)
        .okHttpClient {
            OkHttpClient.Builder()
                .cache(CoilUtils.createDefaultCache(applicationContext))
                .addNetworkInterceptor(ThrottleFailedThumbnailsInterceptor)
                .build()
        }
        .build()

    fun loadCover(game: Game, imageView: ImageView?) {
        if (imageView == null) return

        imageView.load(game.coverFrontUrl, imageLoader) {
            val fallbackDrawable = getFallbackDrawable(game)
            fallback(fallbackDrawable)
            error(fallbackDrawable)
        }
    }

    fun cancelRequest(imageView: ImageView) {
        // coil-kt automatically does that for us.
    }

    companion object {
        fun getFallbackDrawable(game: Game) =
            TextDrawable(computeTitle(game), computeColor(game))

        fun getFallbackRemoteUrl(game: Game): String {
            val color = Integer.toHexString(computeColor(game)).substring(2)
            val title = computeTitle(game)
            return "https://fakeimg.pl/512x512/$color/fff/?font=bebas&text=$title"
        }

        private fun computeTitle(game: Game): String {
            val sanitizedName = game.title
                .replace(Regex("\\(.*\\)"), "")

            return sanitizedName.asSequence()
                .filter { it.isDigit() or it.isUpperCase() or (it == '&') }
                .take(3)
                .joinToString("")
                .ifBlank { game.title.first().toString() }
                .capitalize()
        }

        private fun computeColor(game: Game): Int {
            return ColorUtils.randomColor(game.title)
        }
    }
}