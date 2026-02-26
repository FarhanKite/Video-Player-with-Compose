package com.raywenderlich.videoplayercompose02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.raywenderlich.videoplayercompose02.ui.theme.VideoPlayerCompose02Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerCompose02Theme {
                SimpleYouTubeApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        VideoPlayerManager.onAppForeground(this)
    }

    override fun onPause() {
        super.onPause()
        VideoPlayerManager.onAppBackground()
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoPlayerManager.release()
    }
}