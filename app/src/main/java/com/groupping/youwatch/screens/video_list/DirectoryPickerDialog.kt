package com.groupping.youwatch.screens.video_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.groupping.youwatch.business_logic.video.DirectoryItem
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemWithWatchingHistory
import com.groupping.youwatch.business_logic.video_groups.DirectoryEntity
import com.groupping.youwatch.screens.common.VideoItemComposable
import com.groupping.youwatch.screens.directories.DirectoryList

@Composable
fun DirectoryPickerDialog(
    video: VideoItem?,
    directoryItems: List<DirectoryItem>,
    currentParentId: Int?,
    onNavigateBackIfNotInRoot: () -> Unit,
    onDirectoryClicked: (DirectoryEntity) -> Unit,
    onDirectorySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Button(
                    onClick = {
                        currentParentId?.let { onDirectorySelected(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Select Directory for")
                }

                video?.let {
                    VideoItemComposable(
                        videoWithHistory = VideoItemWithWatchingHistory(video, null, 0.0),
                        isDirectoryMarked = false,
                        onVideoItemClicked = {},
                        onVideoItemLongClicked = {}
                    )
                }

                DirectoryList(
                    directoryItems = directoryItems,
                    currentParentId = currentParentId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp),
                    onNavigateBackIfNotInRoot = { onNavigateBackIfNotInRoot() },
                    onDirectoryClicked = { checkedDirectory ->
                        onDirectoryClicked(checkedDirectory)
                    },
                    onVideoItemClicked = {},
                    onVideoItemLongClicked = {}
                )
            }
        }
    }
}
