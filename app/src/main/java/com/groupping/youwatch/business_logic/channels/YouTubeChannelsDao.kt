package com.groupping.youwatch.business_logic.channels

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface YouTubeChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelDetailsEntity): Long

    @Query("SELECT * FROM channels")
    suspend fun getAllChannels(): List<ChannelDetailsEntity>

    @Query("SELECT * FROM channels WHERE channelId = :id")
    suspend fun getChannelById(id: String): ChannelDetailsEntity?

    @Query("DELETE FROM channels WHERE channelId = :id")
    suspend fun deleteChannelById(id: String)

    @Query("DELETE FROM channels")
    suspend fun deleteAllChannels()
}