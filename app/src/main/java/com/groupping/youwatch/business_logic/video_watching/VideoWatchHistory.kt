package com.groupping.youwatch.business_logic.video_watching

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_watch_history")
data class VideoWatchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoId: String,
    val startTime: Long,
    val durationWatched: Long,
    val stoppedAt: Float,
    val isCompleted: Boolean
)