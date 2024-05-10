package com.mozhimen.emulatork.test.feature.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.test.utils.livedata.CombinedLiveData

/**
 * @ClassName SearchViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SearchViewModel(private val retrogradeDb: RetrogradeDatabase) : ViewModel() {

    class Factory(val retrogradeDb: RetrogradeDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SearchViewModel(retrogradeDb) as T
        }
    }

    val queryString = MutableLiveData<String>()

    val searchResults: LiveData<PagedList<Game>> = queryString.switchMap {
        LivePagedListBuilder(retrogradeDb.gameSearchDao().search(it), 20).build()
    }

    val emptyViewVisible = CombinedLiveData(queryString, searchResults) { query, results ->
        query?.isNotBlank() == true && results?.isEmpty() == true
    }
}