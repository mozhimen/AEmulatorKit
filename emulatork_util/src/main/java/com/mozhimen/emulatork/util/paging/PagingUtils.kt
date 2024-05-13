package com.mozhimen.emulatork.util.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

/**
 * @ClassName PagingUtils
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
fun <T : Any> buildFlowPaging(
    pageSize: Int,
    source: () -> PagingSource<Int, T>
): Flow<PagingData<T>> {
    return Pager(PagingConfig(pageSize), pagingSourceFactory = source).flow
}
