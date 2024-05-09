package com.mozhimen.gamek.emulator.basic.core

import android.net.Uri
import android.os.Build
import android.os.Environment
import com.mozhimen.basick.BuildConfig
import com.mozhimen.gamek.emulator.basic.storage.DirectoriesManager
import io.reactivex.Single
import okio.buffer
import okio.sink
import okio.source
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import java.util.zip.ZipInputStream

/**
 * @ClassName CoreManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
class CoreManager(directoriesManager: DirectoriesManager, retrofit: Retrofit) {

    private val baseUri = Uri.parse("https://buildbot.libretro.com/")
    private val coresUri = baseUri.buildUpon()
        .appendEncodedPath("nightly/android/latest/")
        .appendPath(Build.SUPPORTED_ABIS.first())
        .build()

    private val api = retrofit.create(CoreManagerApi::class.java)

    private val coresDir = directoriesManager.getCoresDirectory()

    init {
        coresDir.mkdirs()
    }

    fun downloadCore(zipFileName: String): Single<File> {
        if (BuildConfig.DEBUG) {
            val overrideFile = File(Environment.getExternalStorageDirectory(), "libretro.so")
            if (overrideFile.exists()) {
                val overrideDestFile = File(coresDir, overrideFile.name)
                overrideFile.copyTo(overrideDestFile, true)
                return Single.just(overrideDestFile)
            }
        }

        val libFileName = zipFileName.substringBeforeLast(".zip")
        val destFile = File(coresDir, "lib$libFileName")

        if (destFile.exists()) {
            return Single.just(destFile)
        }

        val uri = coresUri.buildUpon()
            .appendPath(zipFileName)
            .build()

        return api.downloadZip(uri.toString())
            .map { response ->
                if (!response.isSuccessful) {
                    throw Exception(response.errorBody()!!.string())
                }
                val zipStream = response.body()!!
                while (true) {
                    val entry = zipStream.nextEntry ?: break
                    if (entry.name == libFileName) {
                        zipStream.source().use { zipSource ->
                            destFile.sink().use { fileSink ->
                                zipSource.buffer().readAll(fileSink)
                                return@map destFile
                            }
                        }
                    }
                }
                throw Exception("Library not found in zip")
            }
    }

    private interface CoreManagerApi {

        @GET
        @Streaming
        fun downloadZip(@Url url: String): Single<Response<ZipInputStream>>
    }
}
