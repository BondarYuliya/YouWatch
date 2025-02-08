import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.screens.video_player.VideoPlayerViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@Composable
fun VideoPlayerScreen(video: VideoItem) {
    val viewModel: VideoPlayerViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    VideoPlayerScreenMain(
        lifecycleOwner = lifecycleOwner,
        videoId = video.id.videoId,
        onStartWatching = { viewModel.startWatching(video) },
        onStoppedAt = { second -> viewModel.stoppedAt(second)},
        onStopWatching = { isFinished -> viewModel.stopWatching(video, isFinished) },
        onDurationFetched = { duration -> viewModel.onVideoDurationFetched(video, duration) })

    BackHandler {
        viewModel.stopWatching(video)
        viewModel.goBack()
    }

}


@Composable
fun VideoPlayerScreenMain(
    lifecycleOwner: LifecycleOwner,
    videoId: String,
    onStartWatching: () -> Unit,
    onStoppedAt: (Float) -> Unit,
    onStopWatching: (Boolean) -> Unit,
    onDurationFetched: (Float) -> Unit
) {
    AndroidView(
        factory = { context ->
            val youTubePlayerView = YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
            }

            lifecycleOwner.lifecycle.addObserver(youTubePlayerView)

            youTubePlayerView.initialize(object : AbstractYouTubePlayerListener() {

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0f)
                    onStartWatching()
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    super.onCurrentSecond(youTubePlayer, second)
                    onStoppedAt(second)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    super.onStateChange(youTubePlayer, state)
                    if (state == PlayerConstants.PlayerState.ENDED) {
                        onStopWatching(true)
                    }
                }

                override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                    super.onVideoDuration(youTubePlayer, duration)
                    onDurationFetched(duration)
                }
            })

            youTubePlayerView
        },
        modifier = Modifier.fillMaxSize()
    )
}




