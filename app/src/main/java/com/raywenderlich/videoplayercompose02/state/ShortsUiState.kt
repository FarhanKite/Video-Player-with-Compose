package com.raywenderlich.videoplayercompose02.state

import com.raywenderlich.videoplayercompose02.data.Short

data class ShortsUiState(
    val isLoading: Boolean = true,
    val shorts: List<Short> = emptyList(),
    val initialPageIndex: Int = 0,
    val error: String? = null
)