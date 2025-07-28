package com.groupping.youwatch.business_logic.channels

import android.util.Log
import com.groupping.youwatch.common.network.YouTubeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchChannelDetailsUseCase @Inject constructor(private val youTubeApi: YouTubeApi) {

    suspend fun fetchChannelDetailsFromApi(youtubeId: String): ChannelDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val response = youTubeApi.getChannelDetails(channelId = youtubeId)
                if (response.items.isNotEmpty()) {
                    val channel = response.items.first()
                    ChannelDetails(
                        channelId = youtubeId,
                        name = channel.snippet.title,
                        thumbnailUrl = channel.snippet.thumbnails.medium.url,
                        description = channel.snippet.description,
                        subscriberCount = channel.statistics.subscriberCount.toLong()
                    )
                } else {
                    Log.e(
                        this@FetchChannelDetailsUseCase::class.java.simpleName,
                        "Method youTubeApi.getChannelDetails returned empty itemsList"
                    )
                    null
                }
            } catch (e: Exception) {
                Log.e(this@FetchChannelDetailsUseCase::class.java.simpleName, "Error fetching channel", e)
                null
            }
        }
    }
}