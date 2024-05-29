package com.mozhimen.emulatork.core

import android.content.Context

/**
 * @ClassName CoreUpdater
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
interface CoreDownloader {
    suspend fun downloadCores(context: Context, coreIDs: List<ECoreType>)
}
