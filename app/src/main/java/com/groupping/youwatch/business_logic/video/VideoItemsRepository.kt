package com.groupping.youwatch.business_logic.video

import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryDao
import com.groupping.youwatch.business_logic.video_watching.WatchingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoItemsRepository @Inject constructor(
    private val videoItemDao: VideoItemsDao,
    private val videoWatchHistoryDao: VideoWatchHistoryDao,
    private val watchingUtils: WatchingUtils
) {

    suspend fun getVideosByChannelId(channelId: Long): List<VideoItemEntity> =
        withContext(Dispatchers.IO) {
            return@withContext videoItemDao.getVideosByChannelId(channelId)
        }

    suspend fun insertVideos(videoItems: List<VideoItemEntity>) = withContext(Dispatchers.IO) {
        videoItemDao.insertVideos(videoItems)
    }

    suspend fun updateDirectory(videoId: String, directoryId: Int) = withContext(Dispatchers.IO) {
        val id = videoItemDao.getIdByVideoId(videoId)
        id?.let {
            videoItemDao.updateVideoDirectory(it, directoryId)
        }
    }

    suspend fun fetchVideosWithWatchHistory(databaseChannelId: Long): List<VideoItemWithWatchingHistory> =
        withContext(Dispatchers.IO) {
            val videoEntities = getVideosByChannelId(databaseChannelId) // Fetch video items
            val videoItemsWithHistory = videoEntities.map { videoEntity ->
                val watchHistory =
                    videoWatchHistoryDao.getWatchHistory(videoEntity.videoId) // Fetch watch history
                val videoItem = videoEntity.toVideoItem()
                val duration = videoItem.duration
                val watchingPercentage = if (watchHistory != null && duration != null) {
                    watchingUtils.getWatchedPercent(watchHistory, duration.toInt()) ?: 0.0
                } else {
                    0.0
                }

                VideoItemWithWatchingHistory(
                    videoItem = videoItem, // Convert VideoItemEntity to VideoItem
                    watchHistory = watchHistory,
                    watchedPercent = watchingPercentage
                )
            }
            return@withContext videoItemsWithHistory
        }
}