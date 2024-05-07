package com.mozhimen.gamek.emulator.basic.db.commons

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mozhimen.gamek.emulator.basic.db.mos.Game
import com.mozhimen.gamek.emulator.basic.db.mos.GameLibraryCounts
import io.reactivex.Maybe
import io.reactivex.Single
import org.intellij.lang.annotations.Language

/**
 * @ClassName GameDao
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@Dao
interface GameDao {

    @Language("RoomSql")
    @Query("""
        SELECT
            count(*) totalCount,
            sum(CASE WHEN isFavorite = 1 THEN 1 ELSE 0 END) favoritesCount,
            sum(CASE WHEN lastPlayedAt IS NOT NULL THEN 1 ELSE 0 END) recentsCount
        FROM games
        """)
    fun selectCounts(): Single<GameLibraryCounts>

    @Query("SELECT * FROM games ORDER BY title ASC, id DESC")
    fun selectAll(): DataSource.Factory<Int, Game>

    @Query("SELECT * FROM games WHERE id = :id")
    fun selectById(id: Int): Maybe<Game>

    @Query("SELECT * FROM games WHERE fileUri = :fileUri")
    fun selectByFileUri(fileUri: String): Maybe<Game>

    @Query("SELECT * FROM games WHERE lastIndexedAt < :lastIndexedAt")
    fun selectByLastIndexedAtLessThan(lastIndexedAt: Long): List<Game>

    @Query("SELECT * FROM games WHERE lastPlayedAt IS NOT NULL ORDER BY lastPlayedAt DESC")
    fun selectRecentlyPlayed(): DataSource.Factory<Int, Game>

    @Query("SELECT * FROM games WHERE isFavorite = 1 ORDER BY lastPlayedAt DESC")
    fun selectFavorites(): DataSource.Factory<Int, Game>

    @Query("SELECT * FROM games WHERE lastPlayedAt IS NOT NULL AND isFavorite = 0 ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun selectFirstRecents(limit: Int): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE isFavorite = 1 ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun selectFirstFavorites(limit: Int): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE lastPlayedAt IS NULL LIMIT :limit")
    fun selectFirstNotPlayed(limit: Int): LiveData<List<Game>>

    @Query("SELECT * FROM games WHERE systemId = :systemId ORDER BY title ASC, id DESC")
    fun selectBySystem(systemId: String): DataSource.Factory<Int, Game>

    @Query("SELECT DISTINCT systemId FROM games ORDER BY systemId ASC")
    fun selectSystems(): Single<List<String>>

    @Insert
    fun insert(game: Game)

    @Insert
    fun insert(games: List<Game>)

    @Delete
    fun delete(games: List<Game>)

    @Update
    fun update(game: Game)

    @Update
    fun update(games: List<Game>)
}