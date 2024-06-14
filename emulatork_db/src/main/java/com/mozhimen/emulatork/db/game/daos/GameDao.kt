package com.mozhimen.emulatork.db.game.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mozhimen.emulatork.db.game.entities.Game
import kotlinx.coroutines.flow.Flow

/**
 * @ClassName GameDao
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE id = :id")
    fun selectById(id: Int): Game?

    @Query("SELECT * FROM games WHERE fileUri = :fileUri")
    fun selectByFileUri(fileUri: String): Game?

    @Query("SELECT * FROM games WHERE lastIndexedAt < :lastIndexedAt")
    fun selectByLastIndexedAtLessThan(lastIndexedAt: Long): List<Game>

    @Query("SELECT * FROM games WHERE isFavorite = 1 ORDER BY title ASC")
    fun selectFavorites(): PagingSource<Int, Game>

    @Query(
        """
        SELECT * FROM games WHERE lastPlayedAt IS NOT NULL AND isFavorite = 0 ORDER BY lastPlayedAt DESC LIMIT :limit
        """
    )
    fun selectFirstUnfavoriteRecents(limit: Int): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE isFavorite = 1 ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun selectFirstFavoritesRecents(limit: Int): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE lastPlayedAt IS NOT NULL ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun asyncSelectFirstRecents(limit: Int): List<Game>

    @Query("SELECT * FROM games WHERE isFavorite = 1 ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun selectFirstFavorites(limit: Int): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE lastPlayedAt IS NULL LIMIT :limit")
    fun selectFirstNotPlayed(limit: Int): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE systemName = :systemName ORDER BY title ASC, id DESC")
    fun selectBySystem(systemName: String): PagingSource<Int, Game>

    @Query("SELECT * FROM games WHERE systemName IN (:systemNames) ORDER BY title ASC, id DESC")
    fun selectBySystems(systemNames: List<String>): PagingSource<Int, Game>

    @Query("SELECT DISTINCT systemName FROM games ORDER BY systemName ASC")
    fun selectSystems(): List<String>

    @Query("SELECT count(*) count, systemName systemName FROM games GROUP BY systemName")
    fun selectSystemsWithCount(): Flow<List<SystemCount>>

    @Insert
    fun insert(games: List<Game>): List<Long>

    @Delete
    fun delete(games: List<Game>)

    @Update
    fun update(game: Game)

    @Update
    fun update(games: List<Game>)
}

data class SystemCount(val systemName: String, val count: Int)
