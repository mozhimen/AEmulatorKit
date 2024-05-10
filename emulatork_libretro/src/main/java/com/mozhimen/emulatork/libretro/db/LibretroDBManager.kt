package com.mozhimen.emulatork.libretro.db

import android.content.Context
import androidx.room.Room
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Single
import java.util.concurrent.ExecutorService

/**
 * @ClassName LibretroDBManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class LibretroDBManager(context: Context, executorService: ExecutorService) {

    companion object {
        private const val DB_NAME = "libretro-db"
    }

    private val dbRelay = BehaviorRelay.create<LibretroDatabase>()

    val dbReady: Single<LibretroDatabase> = dbRelay.take(1).singleOrError()

    init {
        executorService.execute {
            val db = Room.databaseBuilder(context, LibretroDatabase::class.java, DB_NAME)
                .createFromAsset("libretro-db.sqlite")
                .build()
            dbRelay.accept(db)
        }
    }
}