package com.mozhimen.emulatork.basic.core

import android.content.Context
import com.mozhimen.emulatork.basic.library.CoreID
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * @ClassName CoreUpdater
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
interface CoreUpdater {

    suspend fun downloadCores(context: Context, coreIDs: List<CoreID>)

    interface CoreManagerApi {
        @GET
        @Streaming
        suspend fun downloadFile(@Url url: String): Response<InputStream>

        @GET
        @Streaming
        suspend fun downloadZip(@Url url: String): Response<ZipInputStream>
    }
}
