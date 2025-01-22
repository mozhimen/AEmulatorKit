package com.mozhimen.emulatork.test.hilt.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import com.mozhimen.emulatork.db.game.entities.Game
import com.mozhimen.pagingk.paging3.basic.utils.Paging3Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @ClassName FavoritesViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(retrogradeDb: RetrogradeDatabase) : ViewModel() {

    class Factory(val retrogradeDb: RetrogradeDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(retrogradeDb) as T
        }
    }

    val favorites: Flow<PagingData<Game>> =
        Paging3Util.getPagerFlow(20) { retrogradeDb.gameDao().selectFavorites() }
}
