package com.groupping.youwatch.business_logic.video_groups

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DirectoryDao {
    @Insert
    suspend fun insertDirectory(directory: DirectoryEntity): Long

    @Query("SELECT * FROM directories WHERE id = :id")
    suspend fun getDirectoryById(id: Int): DirectoryEntity?

    @Query("SELECT * FROM directories WHERE parentId = :parentId")
    suspend fun getSubdirectories(parentId: Int): List<DirectoryEntity>

    @Query("SELECT * FROM directories WHERE id = :id")
    suspend fun getInitialDirectory(id: Int = 1): DirectoryEntity?
}