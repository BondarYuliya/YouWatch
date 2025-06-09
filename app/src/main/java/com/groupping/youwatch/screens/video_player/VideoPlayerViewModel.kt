package com.groupping.youwatch.screens.video_player

import android.util.Log
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemsDao
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistory
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryDao
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryItem
import com.groupping.youwatch.business_logic.video_watching.VideoWatchUseCase
import com.groupping.youwatch.screens.common.navigation.NavigationState
import com.groupping.youwatch.screens.common.navigation.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject


@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    navigationState: NavigationState,
    private val videoWatchHistoryDao: VideoWatchHistoryDao,
    private val videoItemsDao: VideoItemsDao,
    private val videoWatchUseCase: VideoWatchUseCase
) : NavigationViewModel(navigationState) {

    private var currentVideo = MutableLiveData<VideoItem>()
    private var currentWatchHistory = MutableLiveData<VideoWatchHistory?>()
    private var currentWatchHistoryItem = MutableLiveData<VideoWatchHistoryItem?>()

    private val _watchState = MutableLiveData(0f)
    val watchState: LiveData<Float> = _watchState

    fun getCurrentWatchHistory(video: VideoItem) {
        viewModelScope.launch {
            val dataBaseHistory = videoWatchHistoryDao.getWatchHistory(video.id.videoId)
            if (dataBaseHistory == null) {
                val historyItem = VideoWatchHistoryItem(
                    watchingDate = System.currentTimeMillis(),
                    watchedSeconds = listOf(0)
                )
                val history = VideoWatchHistory(
                    videoId = video.id.videoId,
                    videoWatchHistoryItems = listOf(historyItem),
                    fullyWatchedTimes = emptyList(),
                )
                val id = videoWatchHistoryDao.insertWatchHistory(history)
                currentWatchHistory.postValue(history.copy(id = id.toInt()))
                currentWatchHistoryItem.postValue(historyItem)
                _watchState.postValue(0f)
            } else {
                val historyItem = getWatchHistoryItem(dataBaseHistory)
                currentWatchHistory.postValue(historyItem.first)
                currentWatchHistoryItem.postValue(historyItem.second)
                val lastSecond = historyItem.second.watchedSeconds.maxOrNull() ?: 0
                _watchState.postValue(lastSecond.toFloat())
            }
        }
    }

    fun stopWatching() {
        checkParametersAndUpdateHistory(null)
    }


    fun watchedAt(currentSecond: Float) {
        checkParametersAndUpdateHistory(currentSecond)
    }

    private fun checkParametersAndUpdateHistory(currentSecond: Float?) {
        val history = currentWatchHistory.value
        val item = currentWatchHistoryItem.value
        val duration = currentVideo.value?.duration

        if (history != null && item != null && duration != null) {
            viewModelScope.launch(Dispatchers.Main) {
                watchHistoryUpdating {
                    if (currentSecond != null) {
                        videoWatchUseCase.performWatchedAtSecond(
                            history,
                            item,
                            currentSecond.toInt(),
                            duration.toInt()
                        )
                    } else {
                        videoWatchUseCase.checkFinishingAndPerformUpdating(
                            history,
                            item,
                            null,
                            duration.toInt()
                        )
                    }
                }
            }
        }
    }


    private suspend fun watchHistoryUpdating(
        performHistoryUpdating: suspend () -> VideoWatchUseCase.WatchingHistoryUpdatingResult
    ) {
        when (val result = performHistoryUpdating()) {
            is VideoWatchUseCase.WatchingHistoryUpdatingResult.WatchingHistoryNoUpdating -> {
                Log.d("VideoPlayerViewModel", "No updating of watching history")
            }

            is VideoWatchUseCase.WatchingHistoryUpdatingResult.WatchingHistoryUpdated -> {
                currentWatchHistoryItem.postValue(result.item)
                currentWatchHistory.postValue(result.history)
            }
        }
    }

    fun onVideoDurationFetched(video: VideoItem, duration: Float) {
        if (currentVideo.value?.duration == null) {
            video.duration = duration
            currentVideo.postValue(video)
            viewModelScope.launch {
                videoItemsDao.updateVideoDurationById(video.databaseId, duration)
            }
        }
    }

    private suspend fun getWatchHistoryItem(dataBaseHistory: VideoWatchHistory): Pair<VideoWatchHistory, VideoWatchHistoryItem> {
        val allWatchedSeconds = dataBaseHistory.videoWatchHistoryItems
            .flatMap { it.watchedSeconds }
            .toSet()
            .sorted()

        val lastMatchedBeforeInterruption = allWatchedSeconds
            .withIndex()
            .takeWhile { it.value == it.index }
            .lastOrNull()
            ?.value
            ?: 0

        val newItem = VideoWatchHistoryItem(
            watchingDate = System.currentTimeMillis(),
            watchedSeconds = listOf(lastMatchedBeforeInterruption)
        )

        val newItems = dataBaseHistory.videoWatchHistoryItems.plus(listOf(newItem))
        val newHistory = dataBaseHistory.copy(videoWatchHistoryItems = newItems)
        videoWatchHistoryDao.updateWatchHistory(newHistory)
        return Pair(newHistory, newItem)
    }
}
