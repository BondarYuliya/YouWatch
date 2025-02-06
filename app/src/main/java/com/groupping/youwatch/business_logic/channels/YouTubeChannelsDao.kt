package com.groupping.youwatch.business_logic.channels

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface YouTubeChannelDao {

    // Insert a new channel into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelDetailsEntity): Long

    // Get all channels from the database
    @Query("SELECT * FROM channels")
    suspend fun getAllChannels(): List<ChannelDetailsEntity>

    // Get a single channel by its ID
    @Query("SELECT * FROM channels WHERE channelId = :id")
    suspend fun getChannelById(id: String): ChannelDetailsEntity?

    // Delete a channel by its ID
    @Query("DELETE FROM channels WHERE channelId = :id")
    suspend fun deleteChannelById(id: String)

    // Delete all channels
    @Query("DELETE FROM channels")
    suspend fun deleteAllChannels()
}