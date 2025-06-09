package com.groupping.youwatch.business_logic.video_watching

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoWatchUseCase @Inject constructor(
    private val videoWatchHistoryDao: VideoWatchHistoryDao,
    private val watchingUtils: WatchingUtils
) {

    sealed class WatchingHistoryUpdatingResult {
        data class WatchingHistoryUpdated(
            val history: VideoWatchHistory,
            val item: VideoWatchHistoryItem
        ) :
            WatchingHistoryUpdatingResult()

        data object WatchingHistoryNoUpdating : WatchingHistoryUpdatingResult()
    }

    suspend fun performWatchedAtSecond(
        history: VideoWatchHistory,
        item: VideoWatchHistoryItem,
        currentSecond: Int,
        duration: Int
    ): WatchingHistoryUpdatingResult = withContext(Dispatchers.IO) {

        val latestSecond = item.watchedSeconds.maxOrNull() ?: 0
        val difference = currentSecond - latestSecond

        return@withContext when {

            duration != 0 && currentSecond in (duration - 10)..duration -> {
                if (isVideoWatchingFinished(history, duration)) {
                    onVideoWatchingFinished(currentSecond, item, history)
                } else if (difference == 1) {
                    addSecondToWatchHistoryItem(history, item, currentSecond)
                } else {
                    WatchingHistoryUpdatingResult.WatchingHistoryNoUpdating
                }
            }

            difference == 1 -> {
                addSecondToWatchHistoryItem(history, item, currentSecond)
            }

            difference != 0 -> {
                if (difference < -15 || difference > 1) {
                    if (currentSecond <= 5 && isVideoWatchingFinished(history, duration)) {
                        onVideoWatchingFinished(currentSecond, item, history)
                    } else {
                        addNewWatchHistoryItem(history, currentSecond)
                    }
                } else {
                    WatchingHistoryUpdatingResult.WatchingHistoryNoUpdating
                }
            }

            else -> {
                WatchingHistoryUpdatingResult.WatchingHistoryNoUpdating
            }
        }
    }

    private suspend fun onVideoWatchingFinished(
        currentSecond: Int?,
        item: VideoWatchHistoryItem,
        history: VideoWatchHistory
    ): WatchingHistoryUpdatingResult {

        val newFullyWatchedTimes = history.fullyWatchedTimes.plus(
            listOf(item.watchingDate)
        )

        val itemsList = currentSecond?.let { listOf(it) } ?: emptyList()
        val newItem = VideoWatchHistoryItem(System.currentTimeMillis(), itemsList)
        val newHistory = history.copy(
            videoWatchHistoryItems = listOf(newItem),
            fullyWatchedTimes = newFullyWatchedTimes
        )
        return updateWatchHistory(newHistory, newItem)
    }

    private suspend fun addSecondToWatchHistoryItem(
        history: VideoWatchHistory,
        item: VideoWatchHistoryItem,
        currentSecond: Int
    ): WatchingHistoryUpdatingResult {

        val newSecondsList = item.watchedSeconds + currentSecond
        val newItem = item.copy(watchedSeconds = newSecondsList)

        val itemIndex = history.videoWatchHistoryItems.indexOf(item)
        if (itemIndex == -1) {
            throw IllegalArgumentException("Item not found in watch history.")
        }

        val newItemsList = history.videoWatchHistoryItems.toMutableList().apply {
            this[itemIndex] = newItem
        }

        val newHistory = history.copy(videoWatchHistoryItems = newItemsList)
        return updateWatchHistory(newHistory, newItem)
    }

    private suspend fun addNewWatchHistoryItem(
        history: VideoWatchHistory,
        currentSecond: Int
    ): WatchingHistoryUpdatingResult {
        val newItem = VideoWatchHistoryItem(System.currentTimeMillis(), listOf(currentSecond))
        val newItemsList = history.videoWatchHistoryItems.plus(listOf(newItem))
        val newHistory = history.copy(videoWatchHistoryItems = newItemsList)
        return updateWatchHistory(newHistory, newItem)
    }


    private suspend fun updateWatchHistory(
        history: VideoWatchHistory,
        item: VideoWatchHistoryItem
    ): WatchingHistoryUpdatingResult = withContext(Dispatchers.IO) {
        videoWatchHistoryDao.updateWatchHistory(history)
        return@withContext WatchingHistoryUpdatingResult.WatchingHistoryUpdated(history, item)
    }

    suspend fun checkFinishingAndPerformUpdating(
        history: VideoWatchHistory,
        item: VideoWatchHistoryItem,
        currentSecond: Int?,
        duration: Int
    ): WatchingHistoryUpdatingResult = withContext(Dispatchers.IO) {
        return@withContext if (isVideoWatchingFinished(history, duration)) {
            onVideoWatchingFinished(currentSecond, item, history)
        } else {
            WatchingHistoryUpdatingResult.WatchingHistoryNoUpdating
        }
    }


    private fun isVideoWatchingFinished(history: VideoWatchHistory, duration: Int): Boolean {
        return (watchingUtils.getWatchedPercent(history, duration) ?: 0.0) >= 0.95
    }


}