package com.mozhimen.emulatork.test.feature.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.FragmentActivity
import com.mozhimen.emulatork.test.feature.library.LibraryIndexWork

/**
 * @ClassName StorageFrameworkPickerLauncher
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class StorageFrameworkPickerLauncher : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                this.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_FOLDER)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == REQUEST_CODE_PICK_FOLDER && resultCode == Activity.RESULT_OK) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val preferenceKey = getString(com.mozhimen.emulatork.basic.R.string.pref_key_extenral_folder)

            clearPreviousPersistentUris()

            val newRootUri = resultData?.data
            newRootUri?.let {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            sharedPreferences.edit().apply {
                this.putString(preferenceKey, newRootUri.toString())
                this.apply()
            }

            startLibraryIndexWork()
        }
        finish()
    }

    private fun startLibraryIndexWork() {
        LibraryIndexWork.enqueueUniqueWork(applicationContext)
    }

    private fun clearPreviousPersistentUris() {
        contentResolver.persistedUriPermissions
            .filter { it.isReadPermission }
            .forEach {
                contentResolver.releasePersistableUriPermission(it.uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
    }

    companion object {
        private const val REQUEST_CODE_PICK_FOLDER = 1

        fun pickFolder(context: Context) {
            context.startActivity(Intent(context, StorageFrameworkPickerLauncher::class.java))
        }
    }
}
