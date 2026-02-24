//package com.raywenderlich.videoplayercompose02
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.ui.Modifier
//import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
//import com.raywenderlich.videoplayercompose02.SimpleYouTubeApp
//
//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            MaterialTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    SimpleYouTubeApp()
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        VideoPlayerManager.release()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        VideoPlayerManager.pause()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        VideoPlayerManager.stop()
//    }
//}



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
                // Your navigation here
                SimpleYouTubeApp()
            }
        }
    }

    // ✅ App comes to foreground - recreate player with fresh surface
    override fun onResume() {
        super.onResume()
        VideoPlayerManager.onAppForeground(this)
    }

    // ✅ App goes to background - save state and release player
    override fun onPause() {
        super.onPause()
        VideoPlayerManager.onAppBackground()
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoPlayerManager.release()
    }
}