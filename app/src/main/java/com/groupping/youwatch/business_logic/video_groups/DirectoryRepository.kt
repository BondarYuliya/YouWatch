package com.groupping.youwatch.business_logic.video_groups

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DirectoryRepository @Inject constructor(private val directoryDao: DirectoryDao) {
    suspend fun getDirectoriesByParentId(parentId: Int): List<DirectoryEntity> =
        withContext(Dispatchers.IO)
        {
            return@withContext directoryDao.getSubdirectories(parentId)
        }
}
