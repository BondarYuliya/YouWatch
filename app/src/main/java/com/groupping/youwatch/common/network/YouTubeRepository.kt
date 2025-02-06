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

    @RequiresApi(Build.VERSION_CODES.O)
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


//    private suspend fun fetchVideoDuration(videoId: String): String {
//        return try {
//            val response = youTubeApi.getVideoDetails(videoId = videoId)
//            response.items.firstOrNull()?.contentDetails?.duration ?: "PT0S"
//        } catch (e: Exception) {
//            e.printStackTrace()
//            "PT0S"
//        }
//    }
}