package com.mozhimen.emulatork.util.db

import android.database.Cursor

/**
 * @ClassName CursorUtil
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/14
 * @Version 1.0
 */
fun Cursor.asSequence(): Sequence<Cursor> = generateSequence { if (moveToNext()) this else null }
