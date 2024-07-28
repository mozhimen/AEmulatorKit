package com.mozhimen.emulatork.ext.covers

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.util.CoilUtils
import com.mozhimen.basick.utilk.kotlin.UtilKIntColor
import com.mozhimen.basick.utilk.kotlin.UtilKStringWrapper
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.xmlk.drawablek.DrawableKText
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
                .addNetworkInterceptor(CoverThrottleFailedInterceptor)
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
            DrawableKText(UtilKStringWrapper.getStr_ofCompute(game.title), UtilKIntColor.get_ofRandom(game.title))

        fun getFallbackRemoteUrl(game: Game): String {
            val color = Integer.toHexString(UtilKIntColor.get_ofRandom(game.title)).substring(2)
            val title = UtilKStringWrapper.getStr_ofCompute(game.title)
            return "https://fakeimg.pl/512x512/$color/fff/?font=bebas&text=$title"
        }
    }
}