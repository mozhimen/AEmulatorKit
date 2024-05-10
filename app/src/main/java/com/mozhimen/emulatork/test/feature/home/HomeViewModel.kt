package com.mozhimen.emulatork.test.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase
import com.mozhimen.emulatork.test.feature.library.LibraryIndexMonitor

/**
 * @ClassName HomeViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
class HomeViewModel(appContext: Context, retrogradeDb: RetrogradeDatabase) : ViewModel() {

    companion object {
        const val CAROUSEL_MAX_ITEMS = 10
    }

    class Factory(val appContext: Context, val retrogradeDb: RetrogradeDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(appContext, retrogradeDb) as T
        }
    }

    val favoriteGames = retrogradeDb.gameDao().selectFirstFavorites(CAROUSEL_MAX_ITEMS)

    val discoverGames = retrogradeDb.gameDao().selectFirstNotPlayed(CAROUSEL_MAX_ITEMS)

    val recentGames = retrogradeDb.gameDao().selectFirstRecents(CAROUSEL_MAX_ITEMS)

    val indexingInProgress = LibraryIndexMonitor(appContext).getLiveData()
}