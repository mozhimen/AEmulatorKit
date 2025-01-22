package com.mozhimen.emulatork.test.dagger.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.pagingk.paging3.basic.utils.Paging3Util
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * @ClassName SearchViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(private val retrogradeDb: RetrogradeDatabase) : ViewModel() {

    class Factory(val retrogradeDb: RetrogradeDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(retrogradeDb) as T
        }
    }

    val queryString = MutableStateFlow("")

    val searchResults: Flow<PagingData<Game>> = queryString
        .flatMapLatest {
            Paging3Util.getPagerFlow(20) { retrogradeDb.gameSearchDao().search(it) }
        }
}
