package com.groupping.youwatch.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.groupping.youwatch.business_logic.channels.ChannelDetailsEntity
import com.groupping.youwatch.business_logic.channels.YouTubeChannelDao
import com.groupping.youwatch.business_logic.video.VideoItemEntity
import com.groupping.youwatch.business_logic.video.VideoItemsDao
import com.groupping.youwatch.business_logic.video_groups.DirectoryDao
import com.groupping.youwatch.business_logic.video_groups.DirectoryEntity
import com.groupping.youwatch.business_logic.video_groups.VideoByDirectoriesEntity
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistory
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryDao
import kotlin.concurrent.Volatile

@Database(
    entities = [VideoItemEntity::class,
        ChannelDetailsEntity::class,
        DirectoryEntity::class,
        VideoByDirectoriesEntity::class,
        VideoWatchHistory::class],
    version = 3
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun videoItemsDao(): VideoItemsDao
    abstract fun youtubeChannelsDao(): YouTubeChannelDao
    abstract fun directoryDao(): DirectoryDao
    abstract fun videoWatchHistoryDao(): VideoWatchHistoryDao

    companion object {
        @Volatile
        private var INSTANSE: AppDataBase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the `directories` table
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS directories (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                directoryName TEXT NOT NULL,
                parentId INTEGER
            )
            """.trimIndent()
                )

                // Create the `video_by_directories` table
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

                // Add indices to `video_by_directories`
                database.execSQL("CREATE INDEX IF NOT EXISTS index_video_by_directories_directoryId ON video_by_directories(directoryId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_video_by_directories_channelId ON video_by_directories(channelId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_video_by_directories_videoId ON video_by_directories(videoId)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
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


        fun getDatabase(context: Context): AppDataBase {
            return INSTANSE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANSE = instance
                instance
            }
        }
    }
}