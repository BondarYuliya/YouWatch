package com.groupping.youwatch.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
    version = 5
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun videoItemsDao(): VideoItemsDao
    abstract fun youtubeChannelsDao(): YouTubeChannelDao
    abstract fun directoryDao(): DirectoryDao
    abstract fun videoWatchHistoryDao(): VideoWatchHistoryDao

    companion object {
        @Volatile
        private var INSTANSE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANSE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANSE = instance
                instance
            }
        }
    }
}