package com.raywenderlich.videoplayercompose02.state

import com.raywenderlich.videoplayercompose02.data.HomeItem
import com.raywenderlich.videoplayercompose02.data.Video

data class HomeUiState(
    val homeItems: List<HomeItem> = emptyList(),
    val isLoading: Boolean = true,
    val selectedVideo: Video? = null,
    val autoPlayVideoId: String? = null
)