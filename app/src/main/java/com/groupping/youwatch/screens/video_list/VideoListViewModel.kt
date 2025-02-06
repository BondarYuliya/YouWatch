package com.groupping.youwatch.screens.video_list

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemEntity
import com.groupping.youwatch.business_logic.video.VideoItemsRepository
import com.groupping.youwatch.business_logic.video.toEntity
import com.groupping.youwatch.business_logic.video.toVideoItem
import com.groupping.youwatch.common.network.YouTubeRepository
import com.groupping.youwatch.screens.common.NavigationState
import com.groupping.youwatch.screens.common.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    navigationState: NavigationState,
    private val repository: YouTubeRepository,
    private val videoItemsRepository: VideoItemsRepository,
) : NavigationViewModel(navigationState) {

    private val _youtubeVideos = MutableLiveData<List<VideoItem>>(arrayListOf())
    private val _databaseVideos = MutableLiveData<List<VideoItemEntity>>(arrayListOf())
    val allVideos: LiveData<List<VideoItem>> = MediatorLiveData<List<VideoItem>>().apply {
        addSource(_databaseVideos) { databaseVideos ->
            value = databaseVideos.map { it.toVideoItem()}
        }
    }

    private val _showDirectoryPickerDialog = MutableLiveData(false)
    val showDirectoryPickerDialog: LiveData<Boolean> = _showDirectoryPickerDialog

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchAllVideos(databaseChannelId: Long, channelId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _databaseVideos.postValue(videoItemsRepository.getVideosByChannelId(databaseChannelId))
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
                _youtubeVideos.postValue(result.videos)
            }
            is YouTubeRepository.FetchingVideosResult.Error -> {
                _error.postValue("Failed to load videos: ${result.exception.message}")
            }
            is YouTubeRepository.FetchingVideosResult.Empty -> {
                _error.postValue("No videos found for the channel.")
            }
        }
    }

    private fun findNewVideos(
        databaseVideos: List<VideoItemEntity>,
        youtubeVideos: List<VideoItem>
    ): List<VideoItem> {
        val databaseVideoIds = databaseVideos.map { it.videoId } // Extract videoIds from databaseVideos
        return youtubeVideos.filter { youtubeVideo ->
            youtubeVideo.youtubeVideoId.videoId !in databaseVideoIds
        }
    }

    private suspend fun updateVideosForChannel(
        databaseChannelId: Long,
        databaseVideos: List<VideoItemEntity>,
        youtubeVideos: List<VideoItem>
    ) {
        val newVideos = findNewVideos(databaseVideos, youtubeVideos)
        val newVideoEntities = newVideos.map { it.toEntity(databaseChannelId) }

        if (newVideoEntities.isNotEmpty()) {
            videoItemsRepository.insertVideos(newVideoEntities)
            _databaseVideos.postValue(videoItemsRepository.getVideosByChannelId(databaseChannelId))
        }
    }

    fun videoItemDialogPickerSelected(isSelected: Boolean){
        _showDirectoryPickerDialog.postValue(isSelected)
    }

    fun videoItemDialogPickerSelected(videoItem: VideoItem, directoryId: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            videoItemsRepository.updateDirectory(videoItem.youtubeVideoId.videoId, directoryId)
            val currentVideos = _databaseVideos.value?.toMutableList() ?: mutableListOf()
            val videoIndex = currentVideos.indexOfFirst { it.videoId == videoItem.youtubeVideoId.videoId }
            if (videoIndex != -1) {
                val updatedVideo = currentVideos[videoIndex].copy(directoryId = directoryId)
                currentVideos[videoIndex] = updatedVideo
                _databaseVideos.postValue(currentVideos)
            }
        }
    }

}
