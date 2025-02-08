package com.groupping.youwatch.business_logic.video

import android.os.Build
import androidx.annotation.RequiresApi
import com.groupping.youwatch.business_logic.video_groups.DirectoryEntity

data class VideoItem(
    val databaseId: Int,
    val id: Id,
    val snippet: Snippet,
    val channelId: Long,
    val directoryId: Int? = null,
    var duration: Float? =null
)

data class Id(val videoId: String)
data class Snippet(val title: String, val thumbnails: Thumbnails)
data class Thumbnails(val medium: Thumbnail)
data class Thumbnail(val url: String)

data class YouTubeResponse(
    val items: List<VideoItem>,
    val nextPageToken: String?
)

@RequiresApi(Build.VERSION_CODES.O)
fun VideoItemEntity.toVideoItem(): VideoItem {
    return VideoItem(
        databaseId = this.id,
        id = Id(videoId = this.videoId),
        snippet = Snippet(
            title = this.title,
            thumbnails = Thumbnails(
                medium = Thumbnail(url = this.thumbnailUrl)
            )
        ),
        channelId = this.channelId,
        directoryId = this.directoryId,
        duration = this.duration
    )
}

sealed class ListItem {
    data class Directory(val directory: DirectoryEntity) : ListItem()
    data class Video(val video: VideoItem) : ListItem()
}
