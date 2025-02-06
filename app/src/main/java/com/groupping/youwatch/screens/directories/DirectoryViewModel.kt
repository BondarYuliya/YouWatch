package com.groupping.youwatch.screens.directories

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.groupping.youwatch.business_logic.video.ListItem
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video_groups.DirectoryEntity
import com.groupping.youwatch.business_logic.video_groups.DirectoryManagerUseCase
import com.groupping.youwatch.screens.common.NavigationState
import com.groupping.youwatch.screens.common.NavigationViewModel
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

    private val _currentVideos = MutableLiveData<List<VideoItem>>(emptyList())
    private val _combinedList = MediatorLiveData<List<ListItem>>().apply {
        fun update() {
            val directories = _directories.value?.map { ListItem.Directory(it) } ?: emptyList()
            val videos = _currentVideos.value?.map { ListItem.Video(it) } ?: emptyList()
            value = directories + videos
        }

        addSource(_directories) { update() }
        addSource(_currentVideos) { update() }
    }

    val combinedList: LiveData<List<ListItem>> get() = _combinedList

    private val _currentParentId = MutableLiveData<Int?>(null) // Root by default
    val currentParentId: LiveData<Int?> get() = _currentParentId

    private val _isDialogShown = MutableLiveData(false)
    val isDialogShown: LiveData<Boolean> = _isDialogShown

    private val _newDirectoryName = MutableLiveData(TextFieldValue(""))
    val newDirectoryName: LiveData<TextFieldValue> = _newDirectoryName

    fun setAddButtonDialogVisibility(isShown: Boolean) {
        _isDialogShown.postValue(isShown)
    }



    init {
        fetchInitialDirectory()
    }

    private fun fetchDirectories(parentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            updateCurrentDirectory(directoryManagerUseCase.fetchUsualDirectory(parentId))
        }
    }

    private fun fetchInitialDirectory() {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun navigateToDirectory(parentId: Int?) {
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
        viewModelScope.launch(Dispatchers.IO) {
            _currentParentId.value?.let { currentId ->
                val currentDirectories = _directories.value ?: emptyList()
                updateCurrentDirectory(
                    directoryManagerUseCase.addNewDirectory(
                        currentId,
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
}