package com.mozhimen.emulatork.test.feature.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mozhimen.emulatork.basic.library.GameSystem
import com.mozhimen.emulatork.basic.library.db.RetrogradeDatabase

/**
 * @ClassName SystemsViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class SystemsViewModel(retrogradeDb: RetrogradeDatabase) : ViewModel() {

    class Factory(val retrogradeDb: RetrogradeDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SystemsViewModel(retrogradeDb) as T
        }
    }

    val availableSystems = retrogradeDb.gameDao()
        .selectSystems()
        .map { ids -> ids.map { GameSystem.findById(it) } }
        .map { it.filterNotNull() }
}
