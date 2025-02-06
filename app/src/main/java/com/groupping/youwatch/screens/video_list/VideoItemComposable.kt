package com.groupping.youwatch.screens.video_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.groupping.youwatch.business_logic.video.VideoItem


@Composable
fun VideoItemComposable(
    video: VideoItem,
    isDirectoryMarked: Boolean, // Need to make the video gray
    onVideoItemClicked: (video: VideoItem) -> Unit,
    onVideoItemLongClicked: (video: VideoItem) -> Unit
) {
    val isInactive = video.directoryId != null
    val textColor = if (isDirectoryMarked && isInactive) Color.Gray else MaterialTheme.colorScheme.onSurface
    val thumbnailAlpha = if (isInactive) 0.3f else 1f

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onVideoItemClicked(video)
                    },
                    onLongPress = {
                        onVideoItemLongClicked(video)
                    }
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
                color = textColor, // Gray text for inactive items
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun VideoListScreenMain(
    videoItems: List<VideoItem>,
    onVideoItemClicked: (video: VideoItem) -> Unit,
    onVideoItemLongClicked: (video: VideoItem) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(videoItems.size) { index ->
            val video = videoItems[index]
            VideoItemComposable(
                video = video,
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
