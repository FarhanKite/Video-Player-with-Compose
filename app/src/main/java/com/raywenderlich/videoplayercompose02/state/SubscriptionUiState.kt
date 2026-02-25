package com.raywenderlich.videoplayercompose02.state

import com.raywenderlich.videoplayercompose02.data.Channel
import com.raywenderlich.videoplayercompose02.data.Video

data class SubscriptionUiState(
    val isLoading: Boolean = true,
    val subscribedChannels: List<Channel> = emptyList(),
    val mostRelevantVideos: List<Video> = emptyList(),
    val subscribedVideos: List<Video> = emptyList(),
    val selectedVideo: Video? = null
)