package com.groupping.youwatch.business_logic.video

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoItemsRepository @Inject constructor(private val videoItemDao: VideoItemsDao) {

    suspend fun getVideosByChannelId(channelId: Long): List<VideoItemEntity> = withContext(Dispatchers.IO) {
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
}