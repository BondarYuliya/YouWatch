package com.groupping.youwatch.business_logic.channels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val channelId: String,
    val name: String,
    val thumbnailUrl: String,
    val description: String,
    val subscriberCount: Long
){
    fun toDomainModel(): DatabaseChannelDetails{
        val channelDetails = ChannelDetails(
            channelId = this.channelId,
            name = this.name,
            thumbnailUrl = this.thumbnailUrl,
            description = this.description,
            subscriberCount = this.subscriberCount
        )
        return DatabaseChannelDetails(
            databaseChannelId = this.id,
            channelDetails = channelDetails
        )
    }
}