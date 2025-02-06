package com.groupping.youwatch.business_logic.video_watching

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VideoWatchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(history: VideoWatchHistory)

    @Query("UPDATE video_watch_history SET endTime = :endTime, durationWatched = :duration, stoppedAt = :stoppedAt, isCompleted = :isCompleted WHERE videoId = :videoId")
    suspend fun updateWatchHistory(videoId: String, endTime: Long, duration: Long, stoppedAt: Float, isCompleted: Boolean)

    @Query("SELECT * FROM video_watch_history WHERE videoId = :videoId LIMIT 1")
    suspend fun getWatchHistory(videoId: String): VideoWatchHistory?
}