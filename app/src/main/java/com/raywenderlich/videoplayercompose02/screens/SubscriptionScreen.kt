//package com.raywenderlich.videoplayercompose02.screens
//
//import androidx.compose.material.icons.filled.Close
//import kotlin.collections.filter
//import kotlin.jvm.java
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//import androidx.media3.ui.PlayerView
//import coil.compose.AsyncImage
//import com.raywenderlich.videoplayercompose02.data.Channel
//import com.raywenderlich.videoplayercompose02.data.SubscriptionManager
//import com.raywenderlich.videoplayercompose02.data.Video
//import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//@Composable
//fun SubscriptionScreen() {
//    var allChannels by remember { mutableStateOf<List<Channel>>(emptyList()) }
//    var allVideos by remember { mutableStateOf<List<Video>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//    var selectedVideo by remember { mutableStateOf<Video?>(null) }
//    val subscribedChannels = SubscriptionManager.getSubscribedChannels()
//
//    LaunchedEffect(Unit) {
//        val database = FirebaseDatabase.getInstance()
//
//        val channelsRef = database.getReference("channels")
//        channelsRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val channelList = mutableListOf<Channel>()
//                for (channelSnapshot in snapshot.children) {
//                    val channel = channelSnapshot.getValue(Channel::class.java)
//                    if (channel != null) {
//                        channelList.add(channel)
//                    }
//                }
//                allChannels = channelList
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
//
//        val videosRef = database.getReference("videos")
//        videosRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val videoList = mutableListOf<Video>()
//                for (videoSnapshot in snapshot.children) {
//                    val video = videoSnapshot.getValue(Video::class.java)
//                    if (video != null) {
//                        videoList.add(video)
//                    }
//                }
//                allVideos = videoList
//                isLoading = false
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                isLoading = false
//            }
//        })
//    }
//
//    val subscribedVideos = allVideos.filter { video ->
//        subscribedChannels.any { it.channelName == video.channelName }
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Subscriptions",
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
//                item {
//                    Text(
//                        text = "All Channels",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(16.dp)
//                    )
//
//                }
//
//                item {
//                    LazyRow(
//
//                    ) {
//                        items(subscribedChannels) { channel ->
//                            ChannelItem(channel = channel)
//                        }
//                    }
//                }
//
//                item {
//                    Text(
//                        text = "Most relevant",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//
//                item {
//                    LazyRow(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentPadding = PaddingValues(horizontal = 16.dp),
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        val mostRelevantVideos = subscribedChannels.mapNotNull { channel ->
//                            allVideos.firstOrNull { it.channelName == channel.channelName }
//                        }
//
//                        items(mostRelevantVideos) { video ->
//                            VideoItemCompact(
//                                video = video,
//                                onClick = {
//                                    selectedVideo = video
//                                    VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
//                                }
//                            )
//                        }
//                    }
//                }
//
//                if (subscribedChannels.isNotEmpty()) {
//                    item {
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = "Videos from Subscribed Channels",
//                            style = MaterialTheme.typography.titleLarge,
//                            modifier = Modifier.padding(16.dp)
//                        )
//                    }
//
//                    items(subscribedVideos) { video ->
//                        VideoItemSubscription(
//                            video = video,
//                            onClick = {
//                                selectedVideo = video
//                                VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    selectedVideo?.let { video ->
//        VideoPlayerDialogSubscription(
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
//fun ChannelItem(channel: Channel) {
//    var isSubscribed by remember { mutableStateOf(SubscriptionManager.isSubscribed(channel.channelName)) }
//
//        Column(
//            modifier = Modifier
//                .size(100.dp)
//                .padding(8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            AsyncImage(
//                model = channel.channelAvatar,
//                contentDescription = channel.channelName,
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = channel.channelName,
//                style = MaterialTheme.typography.bodySmall,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                textAlign = TextAlign.Center
//            )
//    }
//}
//
//@Composable
//fun VideoItemCompact(video: Video, onClick: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .width(250.dp)
//            .clickable(onClick = onClick)
//    ) {
//        AsyncImage(
//            model = video.thumbnailUrl,
//            contentDescription = video.title,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(140.dp)
//                .clip(RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = video.title,
//            style = MaterialTheme.typography.bodyMedium,
//            maxLines = 2,
//            overflow = TextOverflow.Ellipsis
//        )
//
//        Text(
//            text = "${video.channelName} • ${video.views}",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis
//        )
//    }
//}
//
//@Composable
//fun VideoItemSubscription(video: Video, onClick: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(bottom = 8.dp)
//    ) {
//        AsyncImage(
//            model = video.thumbnailUrl,
//            contentDescription = video.title,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Row(
//
//        ) {
//            Spacer(modifier = Modifier.width(8.dp))
//            AsyncImage(
//                model = video.channelAvatar,
//                contentDescription = video.title,
//                modifier = Modifier
//                    .width(40.dp)
//                    .height(40.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            Column(
//
//            ) {
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
//        }
//    }
//}
//
//@Composable
//fun VideoPlayerDialogSubscription(video: Video, onDismiss: () -> Unit) {
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
//                        Icon(androidx.compose.material.icons.Icons.Default.Close, "Close")
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.raywenderlich.videoplayercompose02.data.Channel
import com.raywenderlich.videoplayercompose02.data.Video
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.raywenderlich.videoplayercompose02.viewmodel.SubscriptionViewModel

@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel = viewModel()   // ViewModel is injected here automatically
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Subscriptions",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {

                item {
                    Text(
                        text = "All Channels",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                item {
                    LazyRow {
                        items(uiState.subscribedChannels) { channel ->
                            ChannelItem(channel = channel)
                        }
                    }
                }

                item {
                    Text(
                        text = "Most relevant",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.mostRelevantVideos) { video ->
                            VideoItemCompact(
                                video = video,
                                onClick = { viewModel.onVideoSelected(video) }
                            )
                        }
                    }
                }

                if (uiState.subscribedChannels.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Videos from Subscribed Channels",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    items(uiState.subscribedVideos) { video ->
                        VideoItemSubscription(
                            video = video,
                            onClick = { viewModel.onVideoSelected(video) }
                        )
                    }
                }
            }
        }
    }

    uiState.selectedVideo?.let { video ->
        VideoPlayerDialogSubscription(
            video = video,
            onDismiss = { viewModel.onVideoDismissed() }
        )
    }
}

@Composable
fun ChannelItem(channel: Channel) {
    Column(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = channel.channelAvatar,
            contentDescription = channel.channelName,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = channel.channelName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VideoItemCompact(video: Video, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = video.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = video.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "${video.channelName} • ${video.views}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun VideoItemSubscription(video: Video, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 8.dp)
    ) {
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = video.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = video.channelAvatar,
                contentDescription = video.title,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
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
        }
    }
}

@Composable
fun VideoPlayerDialogSubscription(video: Video, onDismiss: () -> Unit) {
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
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = VideoPlayerManager.getPlayer(ctx)
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