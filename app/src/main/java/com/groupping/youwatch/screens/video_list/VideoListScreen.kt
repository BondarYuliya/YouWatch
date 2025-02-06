package com.groupping.youwatch.screens.video_list

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video_groups.toListItems
import com.groupping.youwatch.screens.common.Screen
import com.groupping.youwatch.screens.directories.DirectoryViewModel

@Composable
fun VideoListScreen(databaseChannelId: Long, channelId: String) {
    val viewModel: VideoListViewModel = viewModel()
    val dialogListViewModel: DirectoryViewModel = viewModel()
    val directories by dialogListViewModel.directories.observeAsState(emptyList())
    val listItems = remember(directories) { directories.toListItems() }
    val currentParentId by dialogListViewModel.currentParentId.observeAsState(null)
    val showDirectoryPickerDialog by viewModel.showDirectoryPickerDialog.observeAsState(false)

    var selectedVideoItemForDirectoryPicker by remember { mutableStateOf<VideoItem?>(null) }


    LaunchedEffect(Unit) {
        viewModel.fetchAllVideos(databaseChannelId, channelId)
    }

    val videoItems by viewModel.allVideos.observeAsState(emptyList())

    VideoListScreenMain(videoItems = videoItems,
        onVideoItemClicked = { videoItemClicked ->
            viewModel.navigateTo(
                Screen.VideoPlayerScreen(
                    videoItemClicked
                )
            )
        },
        onVideoItemLongClicked = { videoItemClicked ->
            viewModel.videoItemDialogPickerSelected(
                true
            )
            selectedVideoItemForDirectoryPicker = videoItemClicked
        }
    )

    if (showDirectoryPickerDialog) {
        DirectoryPickerDialog(
            video = selectedVideoItemForDirectoryPicker,
            listItems = listItems,
            currentParentId = currentParentId,
            onNavigateBackIfNotInRoot = {
                dialogListViewModel.getParentOfCurrentParent { parentIdOfCurrentParent ->
                    dialogListViewModel.navigateToDirectory(parentId = parentIdOfCurrentParent)
                }
            },
            onDirectoryClicked = { clickedDirectory ->
                dialogListViewModel.navigateToDirectory(
                    clickedDirectory.id
                )
            },
            onDirectorySelected = {directoryId ->
                viewModel.videoItemDialogPickerSelected(
                    false
                )
                selectedVideoItemForDirectoryPicker?.let {
                    viewModel.videoItemDialogPickerSelected(it, directoryId)
                }
            },

            onDismiss = {
                viewModel.videoItemDialogPickerSelected(
                    false
                )
                selectedVideoItemForDirectoryPicker = null
            }
        )
    }

    BackHandler {
        viewModel.goBack()
    }
}