package com.groupping.youwatch.business_logic.video_watching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface VideoWatchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(history: VideoWatchHistory): Long

    @Update
    suspend fun updateWatchHistory(videoWatchHistory: VideoWatchHistory)

    @Query("SELECT * FROM video_watch_history WHERE videoId = :videoId")
    suspend fun getWatchHistory(videoId: String): List<VideoWatchHistory>

    @Query("SELECT * FROM video_watch_history WHERE videoId = :videoId AND isCompleted = 0 LIMIT 1")
    suspend fun getUncompletedWatchHistory(videoId: String): VideoWatchHistory?
}