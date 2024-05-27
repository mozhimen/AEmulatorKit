package com.mozhimen.emulatork.test.hilt.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.mozhimen.emulatork.basic.game.db.RetrogradeDatabase
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.pagingk.paging3.basic.utils.Paging3Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * @ClassName GamesViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@HiltViewModel
class GamesViewModel @Inject constructor(private val retrogradeDb: RetrogradeDatabase) : ViewModel() {

    class Factory(val retrogradeDb: RetrogradeDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GamesViewModel(retrogradeDb) as T
        }
    }

    val systemIds = MutableStateFlow<List<String>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val games: Flow<PagingData<Game>> = systemIds.flatMapLatest {
        when (it.size) {
            0 -> emptyFlow()
            1 -> Paging3Util.getPagerFlow(20) { retrogradeDb.gameDao().selectBySystem(it.first()) }
            else -> Paging3Util.getPagerFlow(20) { retrogradeDb.gameDao().selectBySystems(it) }
        }
    }
}
