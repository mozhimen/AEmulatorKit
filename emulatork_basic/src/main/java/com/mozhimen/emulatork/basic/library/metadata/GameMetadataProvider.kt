package com.mozhimen.emulatork.basic.library.metadata

import com.gojuno.koptional.Optional
import com.mozhimen.emulatork.basic.library.db.mos.Game
import com.mozhimen.emulatork.basic.storage.StorageFile
import io.reactivex.ObservableTransformer

/**
 * @ClassName GameMetadataProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
interface GameMetadataProvider {

    suspend fun retrieveMetadata(storageFile: StorageFile): GameMetadata?
}
