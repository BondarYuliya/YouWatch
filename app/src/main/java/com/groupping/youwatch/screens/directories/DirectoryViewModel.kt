package com.groupping.youwatch.screens.directories

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.groupping.youwatch.business_logic.video.DirectoryItem
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemWithWatchingHistory
import com.groupping.youwatch.business_logic.video_groups.DirectoryEntity
import com.groupping.youwatch.business_logic.video_groups.DirectoryManagerUseCase
import com.groupping.youwatch.screens.common.navigation.NavigationState
import com.groupping.youwatch.screens.common.navigation.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    navigationState: NavigationState,
    private val directoryManagerUseCase: DirectoryManagerUseCase
) : NavigationViewModel(navigationState) {

    private val _directories = MutableLiveData<List<DirectoryEntity>>(emptyList())
    val directories: LiveData<List<DirectoryEntity>> get() = _directories

    private val _currentVideos = MutableLiveData<List<VideoItemWithWatchingHistory>>(emptyList())
    private val _combinedList = MediatorLiveData<List<DirectoryItem>>().apply {
        fun update() {
            val directories = _directories.value?.map { DirectoryItem.Directory(it) } ?: emptyList()
            val videos = _currentVideos.value?.map { DirectoryItem.Video(it) } ?: emptyList()

            //Log.e("FFFFF", "Updated Video List 1: ${videos.map { it.video.watchHistory?.videoWatchHistoryItems?.durationWatched }}")

            postValue(directories + videos)
        }

        addSource(_directories) { update() }
        addSource(_currentVideos) { update() }
    }

    val combinedList: LiveData<List<DirectoryItem>> = _combinedList

    private val _currentParentId = MutableLiveData<Int?>(null) // Root by default
    val currentParentId: LiveData<Int?> get() = _currentParentId

    private val _isDialogShown = MutableLiveData(false)
    val isDialogShown: LiveData<Boolean> = _isDialogShown

    private val _newDirectoryName = MutableLiveData(TextFieldValue(""))
    val newDirectoryName: LiveData<TextFieldValue> = _newDirectoryName

    fun setAddButtonDialogVisibility(isShown: Boolean) {
        _isDialogShown.postValue(isShown)
    }

    fun navigateToDirectory(parentId: Int?) {
        _combinedList.postValue(emptyList())
        if (parentId != null) {
            fetchDirectories(parentId)
        } else {
            fetchInitialDirectory()
        }
    }

    fun getParentOfCurrentParent(callback: (Int?) -> Unit) {
        viewModelScope.launch {
            directoryManagerUseCase.getParentOfCurrentParent(_currentParentId.value, callback)
        }
    }

    fun onAddDirectoryConfirmed(directoryName: String) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _currentParentId.value?.let { parentId ->
                val currentDirectories = _directories.value ?: emptyList()
                updateCurrentDirectory(
                    directoryManagerUseCase.addNewDirectory(
                        parentId,
                        currentDirectories,
                        directoryName
                    )
                )
            }
        }
    }

    fun onDirectoryNameChanged(directoryName: TextFieldValue) {
        _newDirectoryName.postValue(directoryName)
    }


    private fun fetchDirectories(parentId: Int) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            updateCurrentDirectory(directoryManagerUseCase.fetchUsualDirectory(parentId))
        }
    }

    private fun fetchInitialDirectory() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            updateCurrentDirectory(directoryManagerUseCase.fetchInitialDirectory())
        }
    }

    private fun updateCurrentDirectory(result: DirectoryManagerUseCase.Result) {
        when (result) {
            is DirectoryManagerUseCase.Result.DirectoryContentDefined -> {
                _currentParentId.postValue(result.parentId)
                _directories.postValue(result.directories)
                _currentVideos.postValue(result.videos)
            }
            is DirectoryManagerUseCase.Result.DirectoryContentUnDefined -> {
            }
        }
    }
}