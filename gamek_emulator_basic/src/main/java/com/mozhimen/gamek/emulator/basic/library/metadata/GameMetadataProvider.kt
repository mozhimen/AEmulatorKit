package com.mozhimen.gamek.emulator.basic.library.metadata

import com.gojuno.koptional.Optional
import com.mozhimen.gamek.emulator.basic.library.db.mos.Game
import com.mozhimen.gamek.emulator.basic.storage.StorageFile
import io.reactivex.ObservableTransformer

/**
 * @ClassName GameMetadataProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
interface GameMetadataProvider {

    fun transformer(startedAtMs: Long): ObservableTransformer<StorageFile, Optional<Game>>
}
