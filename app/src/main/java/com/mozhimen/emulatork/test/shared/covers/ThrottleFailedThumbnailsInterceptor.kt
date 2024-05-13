package com.mozhimen.emulatork.test.shared.covers

import android.util.LruCache
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @ClassName ThrottleFailedThumbnailsInterceptor
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object ThrottleFailedThumbnailsInterceptor : Interceptor {

    private val failedThumbnailsStatusCode = LruCache<String, Int>(256 * 1024)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestUrl = chain.request().url.toString()
        val previousFailure = failedThumbnailsStatusCode[requestUrl]
        if (previousFailure != null) {
            throw IOException("Thumbnail previously failed with code: $previousFailure")
        }

        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            failedThumbnailsStatusCode.put(chain.request().url.toString(), response.code)
        }

        return response
    }
}