package com.groupping.youwatch.business_logic.video

import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistory

data class VideoItemWithWatchingHistory(
    val videoItem: VideoItem,
    val watchHistory: List<VideoWatchHistory>
)