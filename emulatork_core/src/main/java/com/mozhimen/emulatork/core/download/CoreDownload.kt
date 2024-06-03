package com.mozhimen.emulatork.core.download

import android.content.Context
import com.mozhimen.emulatork.core.ECoreType
import com.mozhimen.emulatork.core.source.CoreSource
import com.mozhimen.emulatork.core.source.CoreSourceNo
import com.mozhimen.emulatork.core.source.CoreSourcePpsspp

/**
 * @ClassName CoreUpdater
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
interface CoreDownload {
    suspend fun downloadCores(context: Context, eCoreTypes: List<ECoreType>)
}
