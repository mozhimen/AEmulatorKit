package com.mozhimen.emulatork.common.storage

import android.net.Uri
import com.mozhimen.emulatork.basic.game.db.entities.DataFile
import com.mozhimen.emulatork.basic.game.db.entities.Game
import com.mozhimen.emulatork.basic.storage.StorageFile
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

/**
 * @ClassName StorageProvider
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/9
 * @Version 1.0
 */
interface StorageProvider {

    val id: String

    val name: String

    val uriSchemes: List<String>

//    val prefsFragmentClass: Class<out LeanbackPreferenceFragment>?

    val enabledByDefault: Boolean

    fun listBaseStorageFiles(): Flow<List<StorageBaseFile>>

    fun getInputStream(uri: Uri): InputStream?

    fun getStorageFile(storageBaseFile: StorageBaseFile): StorageFile?

    fun getGameRomFiles(
        game: Game,
        dataFiles: List<DataFile>,
        allowVirtualFiles: Boolean
    ): StorageRomFile
}
