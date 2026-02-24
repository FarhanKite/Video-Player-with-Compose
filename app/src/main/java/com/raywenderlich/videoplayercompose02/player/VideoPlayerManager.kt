package com.raywenderlich.videoplayercompose02.player

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

object VideoPlayerManager {

    private var player: ExoPlayer? = null
    private var currentUrl: String? = null
    private var currentPosition: Long = 0L
    private var wasPlaying: Boolean = false

    private val _playerVersion = mutableStateOf(0)
    val playerVersion: State<Int> get() = _playerVersion

    fun getPlayer(context: Context): ExoPlayer {
        if (player == null) {
            player = createPlayer(context)
        }
        return player!!
    }

    private fun createPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context.applicationContext).build().apply {
            currentUrl?.let { url ->
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                seekTo(currentPosition)
                playWhenReady = wasPlaying
            }
        }
    }

    fun playVideo(url: String, autoPlay: Boolean = true) {
        currentUrl = url
        currentPosition = 0L
        wasPlaying = autoPlay
        player?.let { exo ->
            val mediaItem = MediaItem.fromUri(url)
            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.playWhenReady = autoPlay
        }
    }

    fun pause() {
        player?.let {
            currentPosition = it.currentPosition
            wasPlaying = false
            it.playWhenReady = false
        }
    }

    fun play() {
        wasPlaying = true
        player?.playWhenReady = true
    }

    fun stop() {
        currentUrl = null
        currentPosition = 0L
        wasPlaying = false
        player?.stop()
        player?.clearMediaItems()
    }

    fun setRepeatMode(repeat: Boolean) {
        player?.repeatMode = if (repeat) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    fun onAppBackground() {
        player?.let {
            currentPosition = it.currentPosition
            wasPlaying = it.isPlaying
            it.playWhenReady = false
        }
        releaseInternal()
    }

    fun onAppForeground(context: Context) {
        releaseInternal()
        player = createPlayer(context)
        _playerVersion.value++
    }

    private fun releaseInternal() {
        player?.release()
        player = null
    }

    fun release() {
        currentUrl = null
        currentPosition = 0L
        wasPlaying = false
        releaseInternal()
    }

    fun resume() {
        wasPlaying = true
        if (wasPlaying) {
            player?.let {
                it.seekTo(currentPosition)
                it.playWhenReady = true
            }
        }
    }
}