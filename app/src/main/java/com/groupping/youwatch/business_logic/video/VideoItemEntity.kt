package com.groupping.youwatch.business_logic.video

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.groupping.youwatch.business_logic.channels.ChannelDetailsEntity
import com.groupping.youwatch.business_logic.video_groups.DirectoryEntity

@Entity(
    tableName = "videos",
    foreignKeys = [
        ForeignKey(
            entity = ChannelDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["channelId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DirectoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["directoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("channelId"), Index("directoryId")]
)

data class VideoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val channelId: Long,
    val directoryId: Int? = null,
    val duration: Float? = null
)

@RequiresApi(Build.VERSION_CODES.O)
fun VideoItem.toEntity(channelId: Long): VideoItemEntity {
    return VideoItemEntity(
        videoId = this.id.videoId,
        title = this.snippet.title,
        thumbnailUrl = this.snippet.thumbnails.medium.url,
        channelId = channelId,
        duration = this.duration
    )
}
