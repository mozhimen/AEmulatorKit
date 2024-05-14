package com.mozhimen.emulatork.basic.storage

import android.net.Uri
import com.mozhimen.emulatork.basic.library.db.mos.DataFile
import com.mozhimen.emulatork.basic.library.db.mos.Game
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

    fun listBaseStorageFiles(): Flow<List<BaseStorageFile>>

    fun getInputStream(uri: Uri): InputStream?

    fun getStorageFile(baseStorageFile: BaseStorageFile): StorageFile?

    fun getGameRomFiles(
        game: Game,
        dataFiles: List<DataFile>,
        allowVirtualFiles: Boolean
    ): RomFiles
}
