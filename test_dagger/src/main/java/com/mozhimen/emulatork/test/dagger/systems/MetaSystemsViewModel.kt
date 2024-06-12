package com.mozhimen.emulatork.test.dagger.systems

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mozhimen.emulatork.basic.system.SystemMetadata
import com.mozhimen.emulatork.common.system.SystemProvider
import com.mozhimen.emulatork.db.game.database.RetrogradeDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.mozhimen.emulatork.common.utils.getSystemMetaType
/**
 * @ClassName MetaSystemsViewModel
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
class MetaSystemsViewModel(retrogradeDb: RetrogradeDatabase, appContext: Context) : ViewModel() {

    class Factory(
        val retrogradeDb: RetrogradeDatabase,
        val appContext: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MetaSystemsViewModel(retrogradeDb, appContext) as T
        }
    }

    val availableMetaSystems: Flow<List<SystemMetadata>> = retrogradeDb.gameDao()
        .selectSystemsWithCount()
        .map { systemCounts ->
            systemCounts.asSequence()
                .filter { (_, count) -> count > 0 }
                .map { (systemId, count) -> SystemProvider.findSysByName(systemId).getSystemMetaType() to count }
                .groupBy { (metaSystemId, _) -> metaSystemId }
                .map { (metaSystemId, counts) -> SystemMetadata(metaSystemId, counts.sumBy { it.second }) }
                .sortedBy { it.getName(appContext) }
                .toList()
        }
}
