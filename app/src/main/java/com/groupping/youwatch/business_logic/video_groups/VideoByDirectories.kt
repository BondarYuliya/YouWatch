package com.groupping.youwatch.business_logic.video_groups

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.groupping.youwatch.business_logic.channels.ChannelDetailsEntity

@Entity(
    tableName = "video_by_directories",
    foreignKeys = [
        ForeignKey(
            entity = DirectoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["directoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChannelDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["channelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("directoryId"), Index("channelId"), Index("videoId")]
)
data class VideoByDirectoriesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Primary key
    val directoryId: Long,                                 // Foreign key to GenreEntity
    val channelId: Int,                             // Foreign key to ChannelDetailsEntity
    val videoId: String                                // ID of the video
) {
    fun toDomainModel(): VideoByDirectories {
        return VideoByDirectories(
            id = this.id,
            directoryId = this.directoryId,
            channelId = this.channelId,
            videoId = this.videoId
        )
    }
}

data class VideoByDirectories(
    val id: Long,
    val directoryId: Long,
    val channelId: Int,
    val videoId: String
)
