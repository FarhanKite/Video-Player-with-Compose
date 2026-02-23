//package com.raywenderlich.videoplayercompose02.player
//
//import android.content.Context
//import android.util.Log
//import androidx.media3.common.MediaItem
//import androidx.media3.common.Player
//import androidx.media3.exoplayer.ExoPlayer
//
//object VideoPlayerManager {
//    private var player: ExoPlayer? = null
//
//    fun getPlayer(context: Context): ExoPlayer {
//        // Check: Do we already have a player?
//        if (player == null) {
//            // No player yet, so create one
//            player = ExoPlayer.Builder(context).build().apply {
//                // Settings for our player
//                repeatMode = Player.REPEAT_MODE_OFF  // Don't loop videos
//                playWhenReady = false                // Don't auto-play when loaded
//            }
//        }
//
//        // Return the player (!! means "I'm sure it's not null")
//        return player!!
//    }
//
//    fun playVideo(videoUrl: String, autoPlay: Boolean = true) {
//        player?.apply {
//            // Stop whatever is currently playing
//            stop()
//
//            // Clear the old video
//            clearMediaItems()
//
//            Log.d("PlayVideo", "I am in play video")
//
//            // Load the new video
//            val mediaItem = MediaItem.fromUri(videoUrl)
//            setMediaItem(mediaItem)
//
//            // Prepare the video (load it, buffer it)
//            prepare()
//
//            // Should we start playing automatically?
//            playWhenReady = autoPlay
//        }
//    }
//
//    fun pause() {
//        player?.pause()
//    }
//
//    fun resume() {
//        player?.play()
//    }
//
//    fun stop() {
//        player?.stop()
//    }
//
//    fun release() {
//        player?.release()
//        player = null
//    }
//
//    fun isPlaying(): Boolean {
//        return player?.isPlaying ?: false
//    }
//
//    fun setRepeatMode(shouldRepeat: Boolean) {
//        player?.repeatMode = if (shouldRepeat) {
//            Player.REPEAT_MODE_ONE  // Loop the current video
//        } else {
//            Player.REPEAT_MODE_OFF  // Play once and stop
//        }
//    }
//}



package com.raywenderlich.videoplayercompose02.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

object VideoPlayerManager {

    private var player: ExoPlayer? = null
    private var currentVideoUrl: String? = null

    fun getPlayer(context: Context): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
            }
        }
        return player!!
    }

    fun playVideo(videoUrl: String, autoPlay: Boolean = true) {
        player?.apply {
            // Always stop and clear previous media
            stop()
            clearMediaItems()

            // Set new media item
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)

            // Prepare and play
            prepare()
            playWhenReady = autoPlay

            currentVideoUrl = videoUrl
        }
    }

    fun pause() {
        player?.pause()
    }

    fun resume() {
        player?.play()
    }

    fun stop() {
        player?.apply {
            stop()
            clearMediaItems()
        }
        currentVideoUrl = null
    }

    fun release() {
        player?.apply {
            stop()
            clearMediaItems()
            release()
        }
        player = null
        currentVideoUrl = null
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    fun setRepeatMode(shouldRepeat: Boolean) {
        player?.repeatMode = if (shouldRepeat) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    fun getCurrentVideoUrl(): String? {
        return currentVideoUrl
    }
}