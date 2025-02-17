package com.groupping.youwatch.screens.video_list

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemWithWatchingHistory
import com.groupping.youwatch.business_logic.video.VideoItemsRepository
import com.groupping.youwatch.business_logic.video.toEntity
import com.groupping.youwatch.common.network.YouTubeRepository
import com.groupping.youwatch.screens.common.navigation.NavigationState
import com.groupping.youwatch.screens.common.navigation.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    navigationState: NavigationState,
    private val repository: YouTubeRepository,
    private val videoItemsRepository: VideoItemsRepository
) : NavigationViewModel(navigationState) {

    private val _youtubeVideos = MutableLiveData<List<VideoItem>>(arrayListOf())
    private val _databaseVideos = MutableLiveData<List<VideoItemWithWatchingHistory>>(arrayListOf())

    val allVideos: LiveData<List<VideoItemWithWatchingHistory>> = _databaseVideos


    private val _showDirectoryPickerDialog = MutableLiveData(false)
    val showDirectoryPickerDialog: LiveData<Boolean> = _showDirectoryPickerDialog

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchAllVideos(databaseChannelId: Long, channelId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _databaseVideos.postValue(videoItemsRepository.fetchVideosWithWatchHistory(databaseChannelId))
            fetchYoutubeVideosInChannel(channelId)
            _youtubeVideos.value?.let { youtubeVideos ->
                updateVideosForChannel(
                    databaseChannelId,
                    _databaseVideos.value.orEmpty(),
                    youtubeVideos
                )
            }
        }
    }

    private suspend fun fetchYoutubeVideosInChannel(channelId: String) {
        when (val result = repository.fetchAllVideos(channelId)) {
            is YouTubeRepository.FetchingVideosResult.Success -> {
                Log.e("GGGGGGGGG", "fetchAllVideos Success: ${result.videos}")
                _youtubeVideos.postValue(result.videos)
            }
            is YouTubeRepository.FetchingVideosResult.Error -> {
                Log.e("GGGGGGGGG", "fetchAllVideos Error: ${result.exception.message}")
                _error.postValue("Failed to load videos: ${result.exception.message}")
            }
            is YouTubeRepository.FetchingVideosResult.Empty -> {
                Log.e("GGGGGGGGG", "fetchAllVideos Empty")
                _error.postValue("No videos found for the channel.")
            }
        }
    }

    private fun findNewVideos(
        databaseVideos: List<VideoItemWithWatchingHistory>,
        youtubeVideos: List<VideoItem>
    ): List<VideoItem> {
        val databaseVideoIds = databaseVideos.map { it.videoItem.id.videoId } // Extract videoIds from databaseVideos
        return youtubeVideos.filter { youtubeVideo ->
            youtubeVideo.id.videoId !in databaseVideoIds
        }
    }

    private suspend fun updateVideosForChannel(
        databaseChannelId: Long,
        databaseVideos: List<VideoItemWithWatchingHistory>,
        youtubeVideos: List<VideoItem>
    ) {
        val newVideos = findNewVideos(databaseVideos, youtubeVideos)
        val newVideoEntities = newVideos.map { it.toEntity(databaseChannelId) }

        if (newVideoEntities.isNotEmpty()) {
            videoItemsRepository.insertVideos(newVideoEntities)
            _databaseVideos.postValue(videoItemsRepository.fetchVideosWithWatchHistory(databaseChannelId))
        }
    }

    fun videoItemDialogPickerSelected(isSelected: Boolean){
        _showDirectoryPickerDialog.postValue(isSelected)
    }

    fun videoItemDialogPickerSelected(videoItem: VideoItem, directoryId: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            videoItemsRepository.updateDirectory(videoItem.id.videoId, directoryId)
            val currentVideos = _databaseVideos.value?.toMutableList() ?: mutableListOf()

            val videoIndex = currentVideos.indexOfFirst { it.videoItem.id.videoId == videoItem.id.videoId }

            if (videoIndex != -1) {
                val updatedVideoItem = currentVideos[videoIndex].videoItem.copy(directoryId = directoryId)
                val updatedVideo = currentVideos[videoIndex].copy(videoItem = updatedVideoItem)
                currentVideos[videoIndex] = updatedVideo
                _databaseVideos.postValue(currentVideos)
            }
        }
    }

}
