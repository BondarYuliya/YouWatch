package com.groupping.youwatch.business_logic.video_watching

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_watch_history")
data class VideoWatchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoId: String,
    val videoWatchHistoryItems: List<VideoWatchHistoryItem>,
    val fullyWatchedTimes: List<Long>
)

data class VideoWatchHistoryItem(
    val watchingDate: Long,
    val watchedSeconds: List<Int>
)

