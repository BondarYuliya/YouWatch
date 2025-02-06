package com.groupping.youwatch.business_logic.channels

data class YouTubeApiResponse(
    val items: List<ChannelItem>
)

data class ChannelItem(
    val snippet: Snippet,
    val statistics: Statistics
)

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val medium: ThumbnailUrl
)

data class ThumbnailUrl(
    val url: String
)

data class Statistics(
    val subscriberCount: String
)

data class ChannelDetails(
    val channelId: String,
    val name: String,
    val thumbnailUrl: String,
    val description: String,
    val subscriberCount: Long
){
    fun convertToRoomEntity(): ChannelDetailsEntity{
        return ChannelDetailsEntity(
            channelId = this.channelId,
            name = this.name,
            thumbnailUrl = this.thumbnailUrl,
            description = this.description,
            subscriberCount = this.subscriberCount
        )
    }
}

data class DatabaseChannelDetails(
    val databaseChannelId: Long,
    val channelDetails: ChannelDetails
)

data class YouTubeVideoResponse(
    val items: List<YouTubeVideoItem>
)

data class YouTubeVideoItem(
    val contentDetails: ContentDetails
)

data class ContentDetails(
    val duration: String
)
