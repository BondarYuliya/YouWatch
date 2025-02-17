package com.groupping.youwatch.business_logic.video_groups

import com.groupping.youwatch.business_logic.video.VideoItemWithWatchingHistory
import com.groupping.youwatch.business_logic.video.VideoItemsDao
import com.groupping.youwatch.business_logic.video.toVideoItem
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DirectoryManagerUseCase @Inject constructor(
    private val directoryDao: DirectoryDao,
    private val videoItemsDao: VideoItemsDao,
    private val videoWatchHistoryDao: VideoWatchHistoryDao
) {

    sealed class Result {
        data class DirectoryContentDefined(
            val parentId: Int,
            val directories: List<DirectoryEntity>,
            val videos: List<VideoItemWithWatchingHistory>
        ) : Result()

        data object DirectoryContentUnDefined : Result()
    }

    suspend fun fetchInitialDirectory(): Result = withContext(Dispatchers.IO) {
        var existingRoot = directoryDao.getInitialDirectory()
        if (existingRoot == null) {
            val rootDirectory = DirectoryEntity(directoryName = "root", parentId = null)
            directoryDao.insertDirectory(rootDirectory).toInt()
            existingRoot = directoryDao.getInitialDirectory()
        }
        existingRoot?.let {
            return@withContext Result.DirectoryContentDefined(
                existingRoot.id,
                directoryDao.getSubdirectories(existingRoot.id),
                getVideosForDirectory(existingRoot.id)
            )
        }
        return@withContext Result.DirectoryContentUnDefined
    }

    suspend fun fetchUsualDirectory(parentId: Int): Result = withContext(Dispatchers.IO) {
        return@withContext Result.DirectoryContentDefined(
            parentId,
            directoryDao.getSubdirectories(parentId),
            getVideosForDirectory(parentId)
        )
    }

    suspend fun addNewDirectory(
        parentId: Int,
        currentDirectories: List<DirectoryEntity>,
        directoryName: String
    ): Result = withContext(Dispatchers.IO) {
        val newDirectory = DirectoryEntity(directoryName = directoryName, parentId = parentId)
        directoryDao.insertDirectory(newDirectory)
        return@withContext Result.DirectoryContentDefined(
            parentId,
            currentDirectories + listOf(newDirectory),
            getVideosForDirectory(parentId)
        )
    }

    suspend fun getParentOfCurrentParent(currentId: Int?, callback: (Int?) -> Unit) =
        withContext(Dispatchers.IO) {
            val parentDirectory = currentId?.let {
                directoryDao.getDirectoryById(it)
            }
            callback(parentDirectory?.parentId)
        }


    private suspend fun getVideosForDirectory(directoryId: Int): List<VideoItemWithWatchingHistory> =
        withContext(Dispatchers.IO) {
            val videoEntityList = videoItemsDao.getVideosByDirectoryId(directoryId)

            val videoItemsWithHistory = videoEntityList.map { videoEntity ->
                val watchHistory = videoWatchHistoryDao.getWatchHistory(videoEntity.videoId)
                VideoItemWithWatchingHistory(
                    videoItem = videoEntity.toVideoItem(),
                    watchHistory = watchHistory
                )
            }
            return@withContext videoItemsWithHistory
        }
}