package com.groupping.youwatch.screens.video_player

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemsDao
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistory
import com.groupping.youwatch.business_logic.video_watching.VideoWatchHistoryDao
import com.groupping.youwatch.screens.common.NavigationState
import com.groupping.youwatch.screens.common.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun startWatching(video: VideoItem) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            startTime.postValue(currentTime)
            val history = videoWatchHistoryDao.getWatchHistory(video.youtubeVideoId.videoId) ?: VideoWatchHistory(
                videoId = video.youtubeVideoId.videoId,
                startTime = currentTime,
                endTime = null,
                durationWatched = 0,
                stoppedAt = 0f,
                isCompleted = false
            )
            videoWatchHistoryDao.insertWatchHistory(history)
        }
    }

    fun stopWatching(video: VideoItem, isCompleted: Boolean=false) {
        viewModelScope.launch {
            val totalDuration = video.duration
            val notWatchedPart = totalDuration?.minus(stoppedAt.value?: 0f)?: 0f

            val currentCompleted = isCompleted || (!isCompleted && notWatchedPart <= 20)
            val currentTime = if (currentCompleted) 0f else stoppedAt.value ?: 0f

            val endTime = System.currentTimeMillis()
            val durationWatched = endTime - (startTime.value?: 0)

            videoWatchHistoryDao.updateWatchHistory(
                video.youtubeVideoId.videoId,
                endTime,
                durationWatched,
                currentTime,
                currentCompleted
            )
        }
    }

    fun stoppedAt(currentSecond: Float) {
        stoppedAt.postValue(currentSecond)
        Log.e("Watching history", "${stoppedAt.value}")
    }

    fun onVideoDurationFetched(video: VideoItem, duration: Float){
        video.duration = duration
        currentVideo.postValue(video)
        viewModelScope.launch {
            videoItemsDao.updateVideoDurationById(video.id, duration)
        }
    }
}