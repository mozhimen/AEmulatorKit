package com.mozhimen.emulatork.db.game.helpers

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * @ClassName Migrations
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
object Migrations {
    val CALLBACK: RoomDatabase.Callback by lazy {
        object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                VERSION_7_8.migrate(db)
            }
        }
    }

    val VERSION_7_8: Migration by lazy {
        object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                CREATE VIRTUAL TABLE fts_games USING FTS4(
                  tokenize=unicode61 "remove_diacritics=1",
                  content="games",
                  title);
                """
                )
                database.execSQL(
                    """
                CREATE TRIGGER games_bu BEFORE UPDATE ON games BEGIN
                  DELETE FROM fts_games WHERE docid=old.id;
                END;
                """
                )
                database.execSQL(
                    """
                CREATE TRIGGER games_bd BEFORE DELETE ON games BEGIN
                  DELETE FROM fts_games WHERE docid=old.id;
                END;
                """
                )
                database.execSQL(
                    """
                CREATE TRIGGER games_au AFTER UPDATE ON games BEGIN
                  INSERT INTO fts_games(docid, title) VALUES(new.id, new.title);
                END;
                """
                )
                database.execSQL(
                    """
                CREATE TRIGGER games_ai AFTER INSERT ON games BEGIN
                  INSERT INTO fts_games(docid, title) VALUES(new.id, new.title);
                END;
                """
                )
                database.execSQL(
                    """
                INSERT INTO fts_games(docid, title) SELECT id, title FROM games;
                """
                )
            }
        }
    }

    val VERSION_8_9: Migration by lazy {
        object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                CREATE TABLE IF NOT EXISTS `datafiles`(
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `gameId` INTEGER NOT NULL,
                    `fileName` TEXT NOT NULL,
                    `fileUri` TEXT NOT NULL,
                    `lastIndexedAt` INTEGER NOT NULL,
                    `path` TEXT, FOREIGN KEY(`gameId`
                ) REFERENCES `games`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE UNIQUE INDEX IF NOT EXISTS `index_datafiles_id` ON `datafiles` (`id`)
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE INDEX IF NOT EXISTS `index_datafiles_fileUri` ON `datafiles` (`fileUri`)
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE INDEX IF NOT EXISTS `index_datafiles_gameId` ON `datafiles` (`gameId`)
                """.trimIndent()
                )

                database.execSQL(
                    """
                CREATE INDEX IF NOT EXISTS `index_datafiles_lastIndexedAt` ON `datafiles` (`lastIndexedAt`)
                """.trimIndent()
                )
            }
        }
    }
}
