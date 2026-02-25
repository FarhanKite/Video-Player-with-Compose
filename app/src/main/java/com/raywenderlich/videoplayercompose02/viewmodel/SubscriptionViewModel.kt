package com.raywenderlich.videoplayercompose02.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.videoplayercompose02.data.SubscriptionManager
import com.raywenderlich.videoplayercompose02.data.Video
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.raywenderlich.videoplayercompose02.repository.SubscriptionRepository
import com.raywenderlich.videoplayercompose02.state.SubscriptionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.raywenderlich.videoplayercompose02.data.Channel

class SubscriptionViewModel(
    private val repository: SubscriptionRepository = SubscriptionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())

    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _allChannels = MutableStateFlow(emptyList<Channel>())
    private val _allVideos = MutableStateFlow(emptyList<Video>())

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                repository.observeChannels().collect { channels ->
                    _allChannels.value = channels
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModelScope.launch {
            try {
                repository.observeVideos().collect { videos ->
                    _allVideos.value = videos
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
            }
        }

        // observing both combinely ... all channels and all videos
        viewModelScope.launch {
            combine(_allChannels, _allVideos) { channels, videos ->
                Pair(channels, videos)
            }.collect { (channels, videos) ->

                if (videos.isEmpty()) return@collect

                val subscribedChannels = SubscriptionManager.getSubscribedChannels()

                val subscribedVideos = videos.filter { video ->
                    subscribedChannels.any { it.channelName == video.channelName }
                }

                val mostRelevantVideos = subscribedChannels.mapNotNull { channel ->
                    videos.firstOrNull { it.channelName == channel.channelName }
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        subscribedChannels = subscribedChannels,
                        subscribedVideos = subscribedVideos,
                        mostRelevantVideos = mostRelevantVideos
                    )
                }
            }
        }
    }

    fun onVideoSelected(video: Video) {
        _uiState.update { it.copy(selectedVideo = video) }
        VideoPlayerManager.playVideo(video.videoUrl, autoPlay = true)
    }

    fun onVideoDismissed() {
        _uiState.update { it.copy(selectedVideo = null) }
        VideoPlayerManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        VideoPlayerManager.stop()
    }
}