//package com.raywenderlich.videoplayercompose02.screens
//
//import kotlin.jvm.java
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//import androidx.media3.ui.PlayerView
//import coil.compose.AsyncImage
//import com.raywenderlich.videoplayercompose02.data.Video
//import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//@Composable
//fun HomeScreen() {
//    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var selectedVideo by remember { mutableStateOf<Video?>(null) }
//    val context = LocalContext.current
//
//    LaunchedEffect(Unit) {
//        val database = FirebaseDatabase.getInstance()
//        val videosRef = database.getReference("videos")
//
//        videosRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val videoList = mutableListOf<Video>()
//                for (videoSnapshot in snapshot.children) {
//                    val video = videoSnapshot.getValue(Video::class.java)
//                    if (video != null) {
//                        videoList.add(video)
//                    }
//                }
//                videos = videoList
//                isLoading = false
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                isLoading = false
//            }
//        })
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Home",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        if (isLoading) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else {
//            LazyColumn {
//                items(videos) { video ->
//                    VideoItem(
//                        video = video,
//                        onClick = {
//                            selectedVideo = video
//                            VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
//                        }
//                    )
//                }
//            }
//        }
//    }
//
//    selectedVideo?.let { video ->
//        VideoPlayerDialog(
//            video = video,
//            onDismiss = {
//                selectedVideo = null
//                VideoPlayerManager.stop()
//            }
//        )
//    }
//}
//
//@Composable
//fun VideoItem(video: Video, onClick: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(8.dp)
//    ) {
//        AsyncImage(
//            model = video.thumbnailUrl,
//            contentDescription = video.title,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .clip(RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = video.title,
//            style = MaterialTheme.typography.titleMedium,
//            maxLines = 2
//        )
//
//        Text(
//            text = "${video.channelName} • ${video.views} • ${video.uploadTime}",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}
//
//@Composable
//fun VideoPlayerDialog(video: Video, onDismiss: () -> Unit) {
//    val context = LocalContext.current
//
//    Dialog(
//        onDismissRequest = onDismiss,
//        properties = DialogProperties(usePlatformDefaultWidth = false)
//    ) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Column(modifier = Modifier.fillMaxSize()) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = video.title,
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.weight(1f)
//                    )
//                    IconButton(onClick = onDismiss) {
//                        Icon(Icons.Default.Close, "Close")
//                    }
//                }
//
//                AndroidView(
//                    factory = { context ->
//                        PlayerView(context).apply {
//                            player = VideoPlayerManager.getPlayer(context)
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(16f / 9f)
//                )
//
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(
//                        text = video.channelName,
//                        style = MaterialTheme.typography.titleSmall
//                    )
//                    Text(
//                        text = "${video.views} • ${video.uploadTime}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
//    }
//}



//
//
//package com.raywenderlich.videoplayercompose02.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//import androidx.media3.ui.PlayerView
//import coil.compose.AsyncImage
//import com.raywenderlich.videoplayercompose02.data.SubscriptionManager
//import com.raywenderlich.videoplayercompose02.data.Video
//import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
////import kotlinx.coroutines.channels.Channel
//import com.raywenderlich.videoplayercompose02.data.Channel
//import kotlin.jvm.java
//
//@Composable
//fun HomeScreen() {
//    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var selectedVideo by remember { mutableStateOf<Video?>(null) }
//    val context = LocalContext.current
//
//    LaunchedEffect(Unit) {
//        try {
//            val database = FirebaseDatabase.getInstance()
//            val videosRef = database.getReference("videos")
//
//            videosRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val videoList = mutableListOf<Video>()
//                    for (videoSnapshot in snapshot.children) {
//                        try {
//                            val video = videoSnapshot.getValue(Video::class.java)
//                            if (video != null) {
//                                videoList.add(video)
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                    videos = videoList
//                    isLoading = false
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    error.toException().printStackTrace()
//                    isLoading = false
//                }
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//            isLoading = false
//        }
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Home",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        if (isLoading) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else {
//            LazyColumn {
//                items(videos) { video ->
//                    VideoItem(
//                        video = video,
//                        onClick = {
//                            selectedVideo = video
//                            VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
//                        }
//                    )
//                }
//            }
//        }
//    }
//
//    selectedVideo?.let { video ->
//        VideoPlayerDialog(
//            video = video,
//            onDismiss = {
//                selectedVideo = null
//                VideoPlayerManager.stop()
//            }
//        )
//    }
//}
//
//@Composable
//fun VideoItem(video: Video, onClick: () -> Unit) {
//    var showMenu by remember { mutableStateOf(false) }
//    var isSubscribed by remember { mutableStateOf(SubscriptionManager.isSubscribed(video.channelName)) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(8.dp)
//    ) {
//        AsyncImage(
//            model = video.thumbnailUrl,
//            contentDescription = video.title,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .clip(RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            AsyncImage(
//                model = video.channelAvatar,
//                contentDescription = video.channelName,
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = video.title,
//                    style = MaterialTheme.typography.titleMedium,
//                    maxLines = 2
//                )
//
//                Text(
//                    text = "${video.channelName} • ${video.views} • ${video.uploadTime}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//
//            Box {
//                IconButton(onClick = { showMenu = true }) {
//                    Icon(Icons.Filled.MoreVert, "Menu")
//                }
//
//                DropdownMenu(
//                    expanded = showMenu,
//                    onDismissRequest = { showMenu = false }
//                ) {
//                    DropdownMenuItem(
//                        text = { Text(if (isSubscribed) "Unsubscribe" else "Subscribe") },
//                        onClick = {
//                            if (isSubscribed) {
//                                SubscriptionManager.unsubscribe(video.channelName)
//                                isSubscribed = false
//                            } else {
//                                val channel = Channel(channelName = video.channelName, channelAvatar = video.channelAvatar)
//                                SubscriptionManager.subscribe(channel)
//                                isSubscribed = true
//                            }
//                            showMenu = false
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun VideoPlayerDialog(video: Video, onDismiss: () -> Unit) {
//    val context = LocalContext.current
//
//    Dialog(
//        onDismissRequest = onDismiss,
//        properties = DialogProperties(usePlatformDefaultWidth = false)
//    ) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Column(modifier = Modifier.fillMaxSize()) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = video.title,
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.weight(1f)
//                    )
//                    IconButton(onClick = onDismiss) {
//                        Icon(Icons.Filled.Close, "Close")
//                    }
//                }
//
//                AndroidView(
//                    factory = { context ->
//                        PlayerView(context).apply {
//                            player = VideoPlayerManager.getPlayer(context)
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(16f / 9f)
//                )
//
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(
//                        text = video.channelName,
//                        style = MaterialTheme.typography.titleSmall
//                    )
//                    Text(
//                        text = "${video.views} • ${video.uploadTime}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//        }
//    }
//}




package com.raywenderlich.videoplayercompose02.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.raywenderlich.videoplayercompose02.data.Channel
import com.raywenderlich.videoplayercompose02.data.SubscriptionManager
import com.raywenderlich.videoplayercompose02.data.Video
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay

@Composable
fun HomeScreen() {
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedVideo by remember { mutableStateOf<Video?>(null) }
    var autoPlayVideoId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val listState = rememberLazyListState()

    // Detect scroll state
    val isScrolling by remember {
        derivedStateOf {
            listState.isScrollInProgress
        }
    }

    // Get most visible video
    val mostVisibleVideoId by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportStart = layoutInfo.viewportStartOffset
            val viewportEnd = layoutInfo.viewportEndOffset
            val viewportCenter = (viewportStart + viewportEnd) / 2

            layoutInfo.visibleItemsInfo
                .filter { it.key.toString().startsWith("video_") }
                .minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
                }
                ?.key
                ?.toString()
                ?.removePrefix("video_")
        }
    }

    LaunchedEffect(Unit) {
        try {
            val database = FirebaseDatabase.getInstance()
            val videosRef = database.getReference("videos")

            videosRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val videoList = mutableListOf<Video>()
                    for (videoSnapshot in snapshot.children) {
                        try {
                            val video = videoSnapshot.getValue(Video::class.java)
                            if (video != null) {
                                videoList.add(video)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    videos = videoList
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                    isLoading = false
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
        }
    }

    // Auto-play logic
    LaunchedEffect(isScrolling, mostVisibleVideoId) {
        if (!isScrolling && mostVisibleVideoId != null) {
            // Wait 500ms after scroll stops before auto-playing
            delay(500)
            if (!isScrolling) {
                autoPlayVideoId = mostVisibleVideoId
                val videoToPlay = videos.find { it.id == mostVisibleVideoId }
                videoToPlay?.let {
                    VideoPlayerManager.playVideo(it.videoUrl, autoPlay = true)
                    VideoPlayerManager.setRepeatMode(false)
                }
            }
        } else if (isScrolling) {
            // Pause when scrolling
            VideoPlayerManager.pause()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = videos,
                    key = { "video_${it.id}" }
                ) { video ->
                    VideoItem(
                        video = video,
                        isAutoPlaying = video.id == autoPlayVideoId,
                        onClick = {
                            selectedVideo = video
                            VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
                        }
                    )
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
                // Show video player
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = VideoPlayerManager.getPlayer(ctx)
                            useController = false
                            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        }
                    },
                    update = { playerView ->
                        playerView.player = VideoPlayerManager.getPlayer(context)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onClick)
                )
            } else {
                // Show thumbnail
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )

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

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isSubscribed) "Unsubscribe" else "Subscribe") },
                        onClick = {
                            if (isSubscribed) {
                                SubscriptionManager.unsubscribe(video.channelName)
                                isSubscribed = false
                            } else {
                                val channel = Channel(
                                    channelName = video.channelName,
                                    channelAvatar = video.channelAvatar
                                )
                                SubscriptionManager.subscribe(channel)
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
                    factory = { context ->
                        PlayerView(context).apply {
                            player = VideoPlayerManager.getPlayer(context)
                            useController = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = video.channelName,
                        style = MaterialTheme.typography.titleSmall
                    )
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