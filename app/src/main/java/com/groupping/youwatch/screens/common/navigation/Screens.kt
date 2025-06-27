package com.groupping.youwatch.screens.common.navigation

import VideoPlayerScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.screens.channels.ChannelsScreen
import com.groupping.youwatch.screens.directories.DirectoryScreen
import com.groupping.youwatch.screens.main.MainScreen
import com.groupping.youwatch.screens.video_list.VideoListScreen


sealed class Screen {
    data object MainScreen : Screen()
    data object ChannelsScreen : Screen()
    data class DirectoryScreen(val directoryId: Int?=null) : Screen()
    data class VideoListScreen(val databaseChannelId: Long, val youtubeChannelId: String) : Screen()
    data class VideoPlayerScreen(val video: VideoItem) : Screen()
}

@Composable
fun App(navigationState: NavigationState) {
    val currentScreen by navigationState.currentScreen.observeAsState(Screen.MainScreen)
    when (currentScreen) {
        is Screen.MainScreen -> MainScreen()
        is Screen.ChannelsScreen -> ChannelsScreen()
        is Screen.DirectoryScreen -> {
            val directoryScreen = currentScreen as Screen.DirectoryScreen
            DirectoryScreen(directoryId = directoryScreen.directoryId)
        }
        is Screen.VideoListScreen -> {
            val videoListScreen = currentScreen as Screen.VideoListScreen
            VideoListScreen(
                databaseChannelId = videoListScreen.databaseChannelId,
                channelId = videoListScreen.youtubeChannelId
            )
        }
        is Screen.VideoPlayerScreen -> {
            val videoPlayerScreen = currentScreen as Screen.VideoPlayerScreen
            VideoPlayerScreen(video = videoPlayerScreen.video)
        }
    }
}