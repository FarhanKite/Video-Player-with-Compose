package com.raywenderlich.videoplayercompose02.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.raywenderlich.videoplayercompose02.data.Short
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.raywenderlich.videoplayercompose02.viewmodel.ShortsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortsScreen(
    initialShortId: String? = null,
    viewModel: ShortsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(initialShortId) {
        viewModel.setInitialShortId(initialShortId)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> VideoPlayerManager.pause()
                Lifecycle.Event.ON_RESUME -> VideoPlayerManager.resume()
                Lifecycle.Event.ON_STOP -> VideoPlayerManager.pause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${uiState.error}")
            }
        }
        uiState.shorts.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No shorts available")
            }
        }
        else -> {
            val pagerState = rememberPagerState(
                initialPage = uiState.initialPageIndex,
                pageCount = { uiState.shorts.size }
            )

            LaunchedEffect(pagerState.currentPage, pagerState.settledPage) {
                val currentShort = uiState.shorts[pagerState.currentPage]
                VideoPlayerManager.playVideo(currentShort.videoUrl, autoPlay = true)
                VideoPlayerManager.setRepeatMode(true)
            }

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val isCurrentPage = page == pagerState.currentPage
                ShortItem(
                    short = uiState.shorts[page],
                    isCurrentPage = isCurrentPage
                )
            }
        }
    }
}

@Composable
fun ShortItem(short: Short, isCurrentPage: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var playerView by remember { mutableStateOf<PlayerView?>(null) }
    var lifecycleState by remember { mutableStateOf(Lifecycle.State.RESUMED) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState = when (event) {
                Lifecycle.Event.ON_RESUME -> Lifecycle.State.RESUMED
                Lifecycle.Event.ON_PAUSE -> Lifecycle.State.STARTED
                else -> lifecycleState
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            playerView?.player = null
            playerView = null
        }
    }

    LaunchedEffect(isCurrentPage, lifecycleState) {
        if (!isCurrentPage) {
            playerView?.player = null
        } else if (lifecycleState == Lifecycle.State.RESUMED) {
            playerView?.player = VideoPlayerManager.getPlayer(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = true
                    controllerAutoShow = false
                    controllerHideOnTouch = true
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                    if (isCurrentPage) {
                        player = VideoPlayerManager.getPlayer(ctx)
                    }

                    playerView = this
                }
            },
            update = { view ->
                if (isCurrentPage && lifecycleState == Lifecycle.State.RESUMED) {
                    if (view.player == null) {
                        view.player = VideoPlayerManager.getPlayer(context)
                    }
                } else if (!isCurrentPage) {
                    view.player = null
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text(
                text = short.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = short.channelName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = short.views,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "${short.likes} likes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}