package com.raywenderlich.videoplayercompose02.screens

import android.view.LayoutInflater
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.raywenderlich.videoplayercompose02.data.HomeItem
import com.raywenderlich.videoplayercompose02.data.Short
import com.raywenderlich.videoplayercompose02.data.SubscriptionManager
import com.raywenderlich.videoplayercompose02.data.Video
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.raywenderlich.videoplayercompose02.viewmodel.HomeViewModel
import kotlin.random.Random

@Composable
fun HomeScreen(
    onNavigateToShorts: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        onDispose { viewModel.onScreenDispose() }
    }

    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    val mostVisibleVideoId by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo
                .filter { it.key.toString().startsWith("video_") }
                .minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - viewportCenter) }
                ?.key?.toString()?.removePrefix("video_")
        }
    }

    LaunchedEffect(isScrolling, mostVisibleVideoId) {
        viewModel.onScrollChanged(isScrolling, mostVisibleVideoId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = uiState.homeItems,
                    key = { item ->
                        when (item) {
                            is HomeItem.VideoItem -> "video_${item.video.id}"
                            is HomeItem.ShortsGridItem -> "shorts_${item.shorts.firstOrNull()?.id ?: Random.nextInt()}"
                        }
                    }
                ) { item ->
                    when (item) {
                        is HomeItem.VideoItem -> {
                            VideoItem(
                                video = item.video,
                                isAutoPlaying = item.video.id == uiState.autoPlayVideoId && uiState.selectedVideo == null,
                                onClick = { viewModel.onVideoSelected(item.video) },
                                onSubscribeToggle = { channelName, channelAvatar ->
                                    viewModel.onSubscribeToggle(channelName, channelAvatar)
                                }
                            )
                        }
                        is HomeItem.ShortsGridItem -> {
                            ShortsGrid2x2(
                                shorts = item.shorts,
                                onShortClick = { shortId -> onNavigateToShorts(shortId) }
                            )
                        }
                    }
                }
            }
        }
    }

    uiState.selectedVideo?.let { video ->
        VideoPlayerDialog(
            video = video,
            onDismiss = { viewModel.onVideoDismissed() }
        )
    }
}

@Composable
fun VideoItem(
    video: Video,
    isAutoPlaying: Boolean,
    onClick: () -> Unit,
    onSubscribeToggle: (channelName: String, channelAvatar: String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isSubscribed by remember { mutableStateOf(SubscriptionManager.isSubscribed(video.channelName)) }
    val context = LocalContext.current
    val playerVersion by VideoPlayerManager.playerVersion

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            if (isAutoPlaying) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = false
                            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                            player = VideoPlayerManager.getPlayer(ctx)
                        }
                    },
                    update = { playerView ->
                        val version = playerVersion
                        playerView.player = VideoPlayerManager.getPlayer(context)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onClick)
                )
            } else {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onClick),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = video.channelAvatar,
                contentDescription = video.channelName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = video.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Text(
                    text = "${video.channelName} • ${video.views} • ${video.uploadTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, "Menu")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(if (isSubscribed) "Unsubscribe" else "Subscribe") },
                        onClick = {
                            onSubscribeToggle(video.channelName, video.channelAvatar)
                            isSubscribed = !isSubscribed
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPlayerDialog(video: Video, onDismiss: () -> Unit) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = true
                            player = VideoPlayerManager.getPlayer(ctx)
                        }
                    },
                    update = { playerView ->
                        val version = VideoPlayerManager.playerVersion.value
                        playerView.player = VideoPlayerManager.getPlayer(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = video.channelName, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${video.views} • ${video.uploadTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ShortsGrid2x2(shorts: List<Short>, onShortClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        Text(
            text = "Shorts",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 650.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(shorts.take(4)) { short ->
                ShortGridItem(short = short, onClick = { onShortClick(short.id) })
            }
        }
    }
}

@Composable
fun ShortGridItem(short: Short, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(9f / 16f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = short.thumbnailUrl,
            contentDescription = short.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = short.title,
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = short.views,
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
            )
        }
    }
}