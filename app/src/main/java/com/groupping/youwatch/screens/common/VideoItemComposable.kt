package com.groupping.youwatch.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.business_logic.video.VideoItemWithWatchingHistory
import com.groupping.youwatch.ui.theme.TextGreen
import com.groupping.youwatch.ui.theme.TextRed
import kotlin.math.round


@Composable
fun VideoItemComposable(
    videoWithHistory: VideoItemWithWatchingHistory,
    isDirectoryMarked: Boolean,
    onVideoItemClicked: (video: VideoItem) -> Unit,
    onVideoItemLongClicked: (video: VideoItem) -> Unit
) {
    val video by remember { mutableStateOf(videoWithHistory.videoItem) }
    val history by remember { mutableStateOf(videoWithHistory.watchHistory) }

    val isInactive = video.directoryId != null
    val textColor =
        if (isDirectoryMarked && isInactive) Color.Gray else MaterialTheme.colorScheme.onSurface
    val thumbnailAlpha = if (isDirectoryMarked && isInactive) 0.3f else 1f

    val completedCount = history?.fullyWatchedTimes?.size ?: 0
    val percentWatched = videoWithHistory.watchedPercent

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onVideoItemClicked(video) },
                    onLongPress = { onVideoItemLongClicked(video) }
                )
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = video.snippet.thumbnails.medium.url),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer(alpha = thumbnailAlpha), // Apply alpha for "inactive" effect
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = video.snippet.title,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (completedCount > 0) {
                Text(
                    text = "Completed: $completedCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGreen
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (percentWatched != 0.0) {
                Text(
                    text = "Watched: ${(percentWatched * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextRed
                )
            }
        }
    }
}

@Composable
fun VideoListScreenMain(
    videoItems: List<VideoItemWithWatchingHistory>,
    onVideoItemClicked: (video: VideoItem) -> Unit,
    onVideoItemLongClicked: (video: VideoItem) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(videoItems.size) { index ->
            val video = videoItems[index]
            VideoItemComposable(
                videoWithHistory = video,
                isDirectoryMarked = true,
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
