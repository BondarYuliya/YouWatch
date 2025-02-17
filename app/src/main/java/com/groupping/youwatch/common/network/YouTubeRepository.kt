package com.groupping.youwatch.common.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.groupping.youwatch.business_logic.video.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class YouTubeRepository @Inject constructor(private val youTubeApi: YouTubeApi) {

    sealed class FetchingVideosResult {
        data class Success(val videos: List<VideoItem>) : FetchingVideosResult()
        data class Error(val exception: Exception) : FetchingVideosResult()
        data object Empty : FetchingVideosResult()
    }

    suspend fun fetchAllVideos(channelId: String): FetchingVideosResult =
        withContext(Dispatchers.IO) {
            var nextPageToken: String? = null
            val videoList = mutableListOf<VideoItem>()

            return@withContext try {
                do {
                    val response = youTubeApi.getChannelVideos(
                        channelId = channelId,
                        pageToken = nextPageToken
                    )

                    videoList.addAll(response.items)
                    nextPageToken = response.nextPageToken

                } while (nextPageToken != null)

                when {
                    videoList.isEmpty() -> FetchingVideosResult.Empty
                    else -> FetchingVideosResult.Success(videoList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                FetchingVideosResult.Error(e)
            }
        }
}