package com.groupping.youwatch.common.network

import com.groupping.youwatch.business_logic.channels.YouTubeApiResponse
import com.groupping.youwatch.business_logic.channels.YouTubeVideoResponse
import com.groupping.youwatch.business_logic.video.YouTubeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {
    @GET("search")
    suspend fun getChannelVideos(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("maxResults") maxResults: Int = 50,
        @Query("type") type: String = "video",
        @Query("pageToken") pageToken: String? = null // Token for pagination
    ): YouTubeResponse

    @GET("channels")
    suspend fun getChannelDetails(
        @Query("part") part: String = "snippet,statistics",
        @Query("id") channelId: String
    ): YouTubeApiResponse

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "contentDetails",
        @Query("id") videoId: String
    ): YouTubeVideoResponse
}