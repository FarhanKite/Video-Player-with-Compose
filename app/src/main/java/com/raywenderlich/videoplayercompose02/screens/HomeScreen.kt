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
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.raywenderlich.videoplayercompose02.data.Channel
import com.raywenderlich.videoplayercompose02.data.HomeItem
import com.raywenderlich.videoplayercompose02.data.Short
import com.raywenderlich.videoplayercompose02.data.SubscriptionManager
import com.raywenderlich.videoplayercompose02.data.Video
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun HomeScreen(
    onNavigateToShorts: (String) -> Unit = {}
) {
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var shorts by remember { mutableStateOf<List<Short>>(emptyList()) }
    var homeItems by remember { mutableStateOf<List<HomeItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedVideo by remember { mutableStateOf<Video?>(null) }
    var autoPlayVideoId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        onDispose {
            VideoPlayerManager.pause()
            autoPlayVideoId = null
        }
    }

    val isScrolling by remember {
        derivedStateOf { listState.isScrollInProgress }
    }

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

    LaunchedEffect(Unit) {
        try {
            val database = FirebaseDatabase.getInstance()

            database.getReference("videos")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val videoList = mutableListOf<Video>()
                        for (videoSnapshot in snapshot.children) {
                            try {
                                val video = videoSnapshot.getValue(Video::class.java)
                                if (video != null) videoList.add(video)
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                        videos = videoList
                        if (shorts.isNotEmpty() || videoList.isNotEmpty()) {
                            homeItems = createMixedFeed(videos, shorts)
                            isLoading = false
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        isLoading = false
                    }
                })

            database.getReference("shorts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val shortList = mutableListOf<Short>()
                        for (shortSnapshot in snapshot.children) {
                            try {
                                val short = shortSnapshot.getValue(Short::class.java)
                                if (short != null) shortList.add(short)
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                        shorts = shortList
                        if (videos.isNotEmpty() || shortList.isNotEmpty()) {
                            homeItems = createMixedFeed(videos, shorts)
                            isLoading = false
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
        }
    }

    LaunchedEffect(isScrolling, mostVisibleVideoId) {
        if (!isScrolling && mostVisibleVideoId != null && selectedVideo == null) {
            delay(500)
            if (!isScrolling && selectedVideo == null) {
                autoPlayVideoId = mostVisibleVideoId
                val videoToPlay = videos.find { it.id == mostVisibleVideoId }
                videoToPlay?.let {
                    VideoPlayerManager.playVideo(it.videoUrl, autoPlay = true)
                    VideoPlayerManager.setRepeatMode(false)
                }
            }
        } else if (isScrolling) {
            VideoPlayerManager.pause()
        }
    }

    LaunchedEffect(selectedVideo) {
        if (selectedVideo != null) autoPlayVideoId = null
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = homeItems,
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
                                isAutoPlaying = item.video.id == autoPlayVideoId && selectedVideo == null,
                                onClick = {
                                    autoPlayVideoId = null
                                    selectedVideo = item.video
                                    VideoPlayerManager.playVideo(item.video.videoUrl, autoPlay = true)
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

    selectedVideo?.let { video ->
        VideoPlayerDialog(
            video = video,
            onDismiss = {
                selectedVideo = null
                VideoPlayerManager.stop()
            }
        )
    }
}

@Composable
fun VideoItem(
    video: Video,
    isAutoPlaying: Boolean,
    onClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isSubscribed by remember { mutableStateOf(SubscriptionManager.isSubscribed(video.channelName)) }
    val context = LocalContext.current

    val playerVersion by VideoPlayerManager.playerVersion

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
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
                            if (isSubscribed) {
                                SubscriptionManager.unsubscribe(video.channelName)
                                isSubscribed = false
                            } else {
                                SubscriptionManager.subscribe(
                                    Channel(channelName = video.channelName, channelAvatar = video.channelAvatar)
                                )
                                isSubscribed = true
                            }
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

    DisposableEffect(Unit) {
        onDispose {
        }
    }

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

fun createMixedFeed(videos: List<Video>, shorts: List<Short>): List<HomeItem> {
    val result = mutableListOf<HomeItem>()
    var videoIndex = 0
    var shortIndex = 0

    while (videoIndex < videos.size || shortIndex < shorts.size) {
        val videosToAdd = Random.nextInt(2, 6).coerceAtMost(videos.size - videoIndex)
        repeat(videosToAdd) {
            if (videoIndex < videos.size) {
                result.add(HomeItem.VideoItem(videos[videoIndex]))
                videoIndex++
            }
        }
        if (shortIndex < shorts.size) {
            val shortsForGrid = mutableListOf<Short>()
            repeat(4) {
                if (shortIndex < shorts.size) {
                    shortsForGrid.add(shorts[shortIndex])
                    shortIndex++
                }
            }
            if (shortsForGrid.isNotEmpty()) result.add(HomeItem.ShortsGridItem(shortsForGrid))
        }
    }
    return result
}