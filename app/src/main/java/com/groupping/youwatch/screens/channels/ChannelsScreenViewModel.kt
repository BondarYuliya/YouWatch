package com.groupping.youwatch.screens.channels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.groupping.youwatch.business_logic.channels.DatabaseChannelDetails
import com.groupping.youwatch.business_logic.channels.FetchChannelDetailsUseCase
import com.groupping.youwatch.business_logic.channels.YouTubeChannelDao
import com.groupping.youwatch.screens.common.NavigationState
import com.groupping.youwatch.screens.common.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsScreenViewModel @Inject constructor(
    navigationState: NavigationState,
    private val fetchChannelDetailsUseCase: FetchChannelDetailsUseCase,
    private val youTubeChannelDao: YouTubeChannelDao
) : NavigationViewModel(navigationState)
{
    private val _channels = MutableLiveData<ArrayList<DatabaseChannelDetails>>(arrayListOf())
    val channels: LiveData<ArrayList<DatabaseChannelDetails>> = _channels

    private val _isDialogShown = MutableLiveData(false)
    val isDialogShown: LiveData<Boolean> = _isDialogShown

    private val _channelId = MutableLiveData(TextFieldValue(""))
    val channelId: LiveData<TextFieldValue> = _channelId

    fun setDialogVisibility(isShown: Boolean){
        _isDialogShown.postValue(isShown)
    }

    init {
        viewModelScope.launch {
            val channelEntities = youTubeChannelDao.getAllChannels()
            val channelDetailsList = channelEntities.map { entity ->
                entity.toDomainModel()
            }
            _channels.postValue(ArrayList(channelDetailsList))
        }

    }

    fun onAddChannelConfirmed(channelId: String){
        viewModelScope.launch {
            val channelDetails = fetchChannelDetailsUseCase.fetchChannelDetailsFromApi(channelId)
            channelDetails?.let { details ->
                val databaseId = youTubeChannelDao.insertChannel(details.convertToRoomEntity())
                updateChannelsList(DatabaseChannelDetails(databaseId, details))
            }
        }
    }

    fun onChannelIdChanged(channelId: TextFieldValue){
        _channelId.postValue(channelId)
    }

    private fun updateChannelsList(details: DatabaseChannelDetails){
        val currentList = _channels.value ?: ArrayList()
        currentList.add(details)
        _channels.postValue(currentList)
    }
}