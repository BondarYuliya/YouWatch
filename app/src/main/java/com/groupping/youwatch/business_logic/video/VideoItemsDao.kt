package com.groupping.youwatch.business_logic.video

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VideoItemsDao {
    @Query("SELECT * FROM videos WHERE channelId = :databaseChannelId")
    suspend fun getVideosByChannelId(databaseChannelId: Long): List<VideoItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videoItems: List<VideoItemEntity>)

    @Query("UPDATE videos SET directoryId = :directoryId WHERE id = :videoId")
    suspend fun updateVideoDirectory(videoId: Int, directoryId: Int?)

    @Query("SELECT id FROM videos WHERE videoId = :videoId LIMIT 1")
    suspend fun getIdByVideoId(videoId: String): Int?

    @Query("SELECT * FROM videos WHERE directoryId = :directoryId")
    suspend fun getVideosByDirectoryId(directoryId: Int): List<VideoItemEntity>

    @Query("UPDATE videos SET duration = :duration WHERE id = :videoId")
    suspend fun updateVideoDurationById(videoId: Int, duration: Float)

}
