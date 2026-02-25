package com.raywenderlich.videoplayercompose02.data

data class Video(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val thumbnailUrl: String = "",
    val videoUrl: String = "",
    val channelName: String = "",
    val channelAvatar: String = "",
    val category: String = "",
    val views: String = "",
    val uploadTime: String = ""
)