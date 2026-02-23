package com.raywenderlich.videoplayercompose02

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.raywenderlich.videoplayercompose02.screens.HomeScreen
import com.raywenderlich.videoplayercompose02.screens.ShortsScreen
import com.raywenderlich.videoplayercompose02.screens.SubscriptionScreen

@Composable
fun SimpleYouTubeApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { navController.navigate("home") }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.PlayArrow, "Shorts") },
                    label = { Text("Shorts") },
                    selected = false,
                    onClick = { navController.navigate("shorts") }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Subscriptions, "Subscriptions") },
                    label = { Text("Subscriptions") },
                    selected = false,
                    onClick = { navController.navigate("subscriptions") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen()
            }

            composable("shorts") {
                ShortsScreen()
            }

            composable("subscriptions") {
                SubscriptionScreen()
            }
        }
    }
}