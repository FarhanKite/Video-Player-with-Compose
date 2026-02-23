//package com.raywenderlich.videoplayercompose02
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import com.raywenderlich.videoplayercompose02.ui.theme.VideoPlayerCompose02Theme
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.ui.Modifier
//import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
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
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    VideoPlayerCompose02Theme {
//        Greeting("Android")
//    }
//}



package com.raywenderlich.videoplayercompose02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.raywenderlich.videoplayercompose02.player.VideoPlayerManager
import com.raywenderlich.videoplayercompose02.SimpleYouTubeApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimpleYouTubeApp()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoPlayerManager.release()
    }

    override fun onPause() {
        super.onPause()
        VideoPlayerManager.pause()
    }

    override fun onStop() {
        super.onStop()
        VideoPlayerManager.stop()
    }
}