package com.groupping.youwatch.screens.video_player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemsDao
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistory
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryDao
import com.groupping.youwatch.screens.common.navigation.NavigationState
import com.groupping.youwatch.screens.common.navigation.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    navigationState: NavigationState,
    private val videoWatchHistoryDao: VideoWatchHistoryDao,
    private val videoItemsDao: VideoItemsDao
) : NavigationViewModel(navigationState) {

    private val startTime = MutableLiveData<Long>(0)
    private var stoppedAt = MutableLiveData<Float>(0f)
    private var currentVideo = MutableLiveData<VideoItem>()
    private var currentWatchHistory = MutableLiveData<VideoWatchHistory?>()

    val watchState: LiveData<Float?> = currentWatchHistory.map {
        it?.stoppedAt
    }

    fun getCurrentWatchHistory(video: VideoItem) {
        viewModelScope.launch {
            val dataBaseHistory = videoWatchHistoryDao.getUncompletedWatchHistory(video.id.videoId)
            if (dataBaseHistory == null) {
                val currentTime = System.currentTimeMillis()
                startTime.postValue(currentTime)
                val history = VideoWatchHistory(
                    videoId = video.id.videoId,
                    startTime = currentTime,
                    durationWatched = 0,
                    stoppedAt = 0f,
                    isCompleted = false
                )
                val id = videoWatchHistoryDao.insertWatchHistory(history)
                currentWatchHistory.postValue(history.copy(id = id.toInt()))
            } else {
                currentWatchHistory.value = dataBaseHistory
            }
        }
    }

    fun stopWatching(video: VideoItem, isCompleted: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val totalDuration = currentVideo.value?.duration
            val notWatchedPart = totalDuration?.minus(stoppedAt.value ?: 0f) ?: 0f

            val currentCompleted =
                isCompleted || (notWatchedPart <= 30) || (totalDuration?.let { notWatchedPart / it > 0.9 }
                    ?: false)
            val currentTime = if (currentCompleted) 0f else stoppedAt.value ?: 0f

            val endTime = System.currentTimeMillis()

            val durationWatched: Long = when {
                currentCompleted -> 100
                totalDuration != null -> (100 * (1 - notWatchedPart / totalDuration)).toLong()
                else -> 0
            }

            currentWatchHistory.value?.let { currentHistory ->
                val updatedWatchHistory = VideoWatchHistory(
                    id = currentHistory.id,
                    videoId = video.id.videoId,
                    startTime = currentHistory.startTime,
                    durationWatched = durationWatched,
                    stoppedAt = currentTime,
                    isCompleted = currentCompleted,
                )
                currentWatchHistory.postValue(updatedWatchHistory)
                videoWatchHistoryDao.updateWatchHistory(updatedWatchHistory)
            }
        }
    }

    fun stoppedAt(currentSecond: Float) {
        val lastStoppedAt = watchState.value ?: 0f
        if (currentSecond > lastStoppedAt) {
            stoppedAt.postValue(currentSecond)
            currentWatchHistory.value = currentWatchHistory.value?.copy(stoppedAt = currentSecond)
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


}