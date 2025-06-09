import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.groupping.youwatch.business_logic.video.VideoItem
import com.groupping.youwatch.screens.common.LifecycleObserving
import com.groupping.youwatch.screens.video_player.VideoPlayerViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@Composable
fun VideoPlayerScreen(video: VideoItem) {
    val viewModel: VideoPlayerViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val watchState by viewModel.watchState.observeAsState(null)
//    Log.e("Observing watchState", "$watchState")

    LifecycleObserving(
        lifecycleOwner = lifecycleOwner,
        eventsList = listOf(
            Lifecycle.Event.ON_RESUME to { viewModel.getCurrentWatchHistory(video) }
        )
    )

    VideoPlayerScreenMain(
        lifecycleOwner = lifecycleOwner,
        videoId = video.id.videoId,
        watchState = watchState,
        onStoppedAt = { second -> viewModel.watchedAt(second) },
        onStopWatching = { viewModel.stopWatching() },
        onDurationFetched = { duration -> viewModel.onVideoDurationFetched(video, duration) }
    )

    BackHandler {
        viewModel.stopWatching()
        viewModel.goBack()
    }
}


@Composable
fun VideoPlayerScreenMain(
    lifecycleOwner: LifecycleOwner,
    videoId: String,
    watchState: Float?, // will be null initially, set later
    onStoppedAt: (Float) -> Unit,
    onStopWatching: () -> Unit,
    onDurationFetched: (Float) -> Unit
) {
    var youTubePlayerRef by remember { mutableStateOf<YouTubePlayer?>(null) }
    var playerReady by remember { mutableStateOf(false) }
    var hasLoadedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(watchState, playerReady) {
        if (!hasLoadedOnce && playerReady && watchState != null) {
            youTubePlayerRef?.loadVideo(videoId, watchState.toFloat())
            hasLoadedOnce = true
        }
    }

    AndroidView(
        factory = { context ->
            val youTubePlayerView = YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
            }

            lifecycleOwner.lifecycle.addObserver(youTubePlayerView)

            youTubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayerRef = youTubePlayer
                    playerReady = true
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    onStoppedAt(second)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    if (state == PlayerConstants.PlayerState.ENDED) {
                        onStopWatching()
                    }
                }

                override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                    onDurationFetched(duration)
                }
            })

            youTubePlayerView
        },
        modifier = Modifier.fillMaxSize()
    )
}





