package com.groupping.youwatch.common.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS directories (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                directoryName TEXT NOT NULL,
                parentId INTEGER
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS video_by_directories (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                directoryId INTEGER NOT NULL,
                channelId TEXT NOT NULL,
                videoId TEXT NOT NULL,
                FOREIGN KEY(directoryId) REFERENCES directories(id) ON DELETE CASCADE,
                FOREIGN KEY(channelId) REFERENCES youtube_channels(channelId) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS index_video_by_directories_directoryId ON video_by_directories(directoryId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_video_by_directories_channelId ON video_by_directories(channelId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_video_by_directories_videoId ON video_by_directories(videoId)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS video_watch_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                videoId TEXT NOT NULL,
                startTime INTEGER NOT NULL,
                endTime INTEGER,
                durationWatched INTEGER NOT NULL,
                stoppedAt REAL NOT NULL,
                isCompleted INTEGER NOT NULL CHECK (isCompleted IN (0,1))
            )
            """.trimIndent()
        )

        database.execSQL("ALTER TABLE videos ADD COLUMN duration TEXT NOT NULL DEFAULT 'PT0S'")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE video_watch_history_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                videoId TEXT NOT NULL,
                startTime INTEGER NOT NULL,
                durationWatched INTEGER NOT NULL,
                stoppedAt REAL NOT NULL,
                isCompleted INTEGER NOT NULL
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            INSERT INTO video_watch_history_new (id, videoId, startTime, durationWatched, stoppedAt, isCompleted)
            SELECT id, videoId, startTime, durationWatched, stoppedAt, isCompleted FROM video_watch_history
            """.trimIndent()
        )

        database.execSQL("DROP TABLE video_watch_history")

        database.execSQL("ALTER TABLE video_watch_history_new RENAME TO video_watch_history")
    }
}


