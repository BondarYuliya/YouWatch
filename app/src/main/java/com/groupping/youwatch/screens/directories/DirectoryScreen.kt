package com.groupping.youwatch.screens.directories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupping.youwatch.business_logic.video.DirectoryItem
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemWithWatchingHistory
import com.groupping.youwatch.business_logic.video_groups.DirectoryEntity
import com.groupping.youwatch.screens.common.AddingButton
import com.groupping.youwatch.screens.common.LifecycleObserving
import com.groupping.youwatch.screens.common.navigation.Screen
import com.groupping.youwatch.screens.common.VideoItemComposable

@Composable
fun DirectoryScreen(directoryId: Int?=null) {
    val viewModel: DirectoryViewModel = viewModel()
    val listItems by rememberUpdatedState(viewModel.combinedList.observeAsState(emptyList()).value)
    val currentParentId by viewModel.currentParentId.observeAsState(null)
    val isDialogShown by viewModel.isDialogShown.observeAsState(false)
    val newDirectoryName by viewModel.newDirectoryName.observeAsState(TextFieldValue(""))
    val lifecycleOwner = LocalLifecycleOwner.current

    LifecycleObserving(
        lifecycleOwner = lifecycleOwner,
        eventsList = listOf(
            Lifecycle.Event.ON_RESUME to { viewModel.navigateToDirectory(directoryId) }
        )
    )

    DirectoryScreenMain(
        addingButtonText = "Add Directory",
        onAddButtonClicked = { viewModel.setAddButtonDialogVisibility(true) },
        directoryItems = listItems,
        currentParentId = currentParentId,
        onNavigateBackIfNotInRoot = {
            viewModel.getParentOfCurrentParent { parentIdOfCurrentParent ->
                viewModel.navigateToDirectory(parentId = parentIdOfCurrentParent)
            }
        },
        onDirectoryClicked = { clickedDirectory -> viewModel.navigateToDirectory(clickedDirectory.id) },
        onVideoItemClicked = { videoItemClicked ->
            viewModel.navigateTo(
                Screen.VideoPlayerScreen(
                    videoItemClicked
                )
            )
        },
        onVideoItemLongClicked = { }
    )

    if (isDialogShown) {
        AddingDirectoryDialog(
            directoryName = newDirectoryName,
            onDialogDismissed = { viewModel.setAddButtonDialogVisibility(false) },
            onAddDirectory = { directoryName -> viewModel.onAddDirectoryConfirmed(directoryName) },
            onValueChanged = { directoryName -> viewModel.onDirectoryNameChanged(directoryName) }
        )
    }

    BackHandler {
        viewModel.goBack()
    }
}

@Composable
fun DirectoryScreenMain(
    addingButtonText: String,
    onAddButtonClicked: () -> Unit,
    directoryItems: List<DirectoryItem>,
    currentParentId: Int?,
    onNavigateBackIfNotInRoot: () -> Unit,
    onDirectoryClicked: (clickedDirectory: DirectoryEntity) -> Unit,
    onVideoItemClicked: (clickedVideo: VideoItem) -> Unit,
    onVideoItemLongClicked: (clickedVideo: VideoItem) -> Unit
) {

    AddingButton(
        text = addingButtonText,
        onAddButtonClicked = { onAddButtonClicked() },
        content = { padding ->
            Box(Modifier.padding(padding)) {
                Column {
                    DirectoryList(
                        directoryItems = directoryItems,
                        currentParentId = currentParentId,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 50.dp),
                        onNavigateBackIfNotInRoot = { onNavigateBackIfNotInRoot() },
                        onDirectoryClicked = { checkedDirectory ->
                            onDirectoryClicked(checkedDirectory)
                        },
                        onVideoItemClicked = { videoItemClicked ->
                            onVideoItemClicked(videoItemClicked)
                        },
                        onVideoItemLongClicked = { videoItemClicked ->
                            onVideoItemLongClicked(
                                videoItemClicked
                            )
                        }
                    )
                }

            }
        }
    )
}


@Composable
fun DirectoryList(
    directoryItems: List<DirectoryItem>,
    currentParentId: Int?,
    modifier: Modifier,
    onNavigateBackIfNotInRoot: () -> Unit,
    onDirectoryClicked: (clickedDirectory: DirectoryEntity) -> Unit,
    onVideoItemClicked: (clickedVideo: VideoItem) -> Unit,
    onVideoItemLongClicked: (clickedVideo: VideoItem) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        if (currentParentId != 1) {
            BasicText(
                text = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onNavigateBackIfNotInRoot() },
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Blue)
            )
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(directoryItems.size) { index ->
                when (val item = directoryItems[index]) {
                    is DirectoryItem.Directory -> DirectoryItem(item.directory) { clickedDirectory ->
                        onDirectoryClicked(clickedDirectory)
                    }

                    is DirectoryItem.Video -> VideoItemComposable(
                        videoWithHistory = item.video,
                        isDirectoryMarked = false,
                        onVideoItemClicked = { videoItemClicked ->
                            onVideoItemClicked(videoItemClicked)
                        },
                        onVideoItemLongClicked = { videoItemClicked ->
                            onVideoItemLongClicked(
                                videoItemClicked
                            )
                        },
                    )
                }
            }
        }
    }
}


@Composable
fun DirectoryItem(
    directory: DirectoryEntity,
    onClick: (clickedDirectory: DirectoryEntity) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(directory) }
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Replace with a directory icon
            contentDescription = "Directory Icon",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        BasicText(
            text = directory.directoryName,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun AddingDirectoryDialog(
    directoryName: TextFieldValue,
    onDialogDismissed: () -> Unit,
    onAddDirectory: (String) -> Unit,
    onValueChanged: (TextFieldValue) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDialogDismissed() },
        title = { Text("Add New Channel") },
        text = {
            Column {
                Text("Enter YouTube Channel ID:")
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = directoryName,
                    onValueChange = { onValueChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        if (directoryName.text.isEmpty()) {
                            Text("Channel ID", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddDirectory(directoryName.text)
                    onDialogDismissed()
                    onValueChanged(TextFieldValue(""))
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDialogDismissed() }) {
                Text("Cancel")
            }
        }
    )
}
