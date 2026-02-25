package com.raywenderlich.videoplayercompose02.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.videoplayercompose02.data.Channel
import com.raywenderlich.videoplayercompose02.data.HomeItem
import com.raywenderlich.videoplayercompose02.data.Short
import com.raywenderlich.videoplayercompose02.data.SubscriptionManager
import com.raywenderlich.videoplayercompose02.data.Video
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.raywenderlich.videoplayercompose02.repository.VideoRepository
import com.raywenderlich.videoplayercompose02.state.HomeUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel(
    private val repository: VideoRepository = VideoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    private val _shorts = MutableStateFlow<List<Short>>(emptyList())

    private var autoPlayJob: Job? = null

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(_videos, _shorts) { videos, shorts ->
                if (videos.isEmpty() && shorts.isEmpty()) {
                    HomeUiState(isLoading = true)
                } else {
                    _uiState.value.copy(
                        homeItems = createMixedFeed(videos, shorts),
                        isLoading = false
                    )
                }
            }.collect { state ->
                _uiState.update { current ->
                    state.copy(
                        selectedVideo = current.selectedVideo,
                        autoPlayVideoId = current.autoPlayVideoId
                    )
                }
            }
        }

        viewModelScope.launch {
            try {
                repository.observeVideos().collect { videos ->
                    _videos.value = videos
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
            }
        }

        viewModelScope.launch {
            try {
                repository.observeShorts().collect { shorts ->
                    _shorts.value = shorts
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onScrollChanged(isScrolling: Boolean, mostVisibleVideoId: String?) {
        if (isScrolling) {
            autoPlayJob?.cancel()
            VideoPlayerManager.pause()
        } else {
            scheduleAutoPlay(mostVisibleVideoId)
        }
    }

    private fun scheduleAutoPlay(videoId: String?) {
        if (videoId == null || _uiState.value.selectedVideo != null) return
        autoPlayJob?.cancel()
        autoPlayJob = viewModelScope.launch {
            delay(500)
            // Re-check conditions after delay
            if (_uiState.value.selectedVideo != null) return@launch
            val video = _videos.value.find { it.id == videoId } ?: return@launch
            _uiState.update { it.copy(autoPlayVideoId = videoId) }
            VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
            VideoPlayerManager.setRepeatMode(false)
        }
    }

    fun onVideoSelected(video: Video) {
        autoPlayJob?.cancel()
        _uiState.update { it.copy(selectedVideo = video, autoPlayVideoId = null) }
        VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
    }

    fun onVideoDismissed() {
        _uiState.update { it.copy(selectedVideo = null) }
        VideoPlayerManager.stop()
    }

    fun onSubscribeToggle(channelName: String, channelAvatar: String) {
        if (SubscriptionManager.isSubscribed(channelName)) {
            SubscriptionManager.unsubscribe(channelName)
        } else {
            SubscriptionManager.subscribe(
                Channel(channelName = channelName, channelAvatar = channelAvatar)
            )
        }
    }

    fun onScreenDispose() {
        autoPlayJob?.cancel()
        VideoPlayerManager.pause()
        _uiState.update { it.copy(autoPlayVideoId = null) }
    }

    override fun onCleared() {
        super.onCleared()
        VideoPlayerManager.stop()
    }

    private fun createMixedFeed(videos: List<Video>, shorts: List<Short>): List<HomeItem> {
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
}