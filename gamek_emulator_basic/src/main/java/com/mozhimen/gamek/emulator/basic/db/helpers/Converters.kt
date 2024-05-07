package com.mozhimen.gamek.emulator.basic.db.helpers

import android.net.Uri
import androidx.room.TypeConverter

/**
 * @ClassName Converters
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
class Converters {

    @TypeConverter
    fun fromUri(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToUri(string: String): Uri = Uri.parse(string)
}