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

data class Short(
    val id: String = "",
    val title: String = "",
    val thumbnailUrl: String = "",
    val videoUrl: String = "",
    val channelName: String = "",
    val channelAvatar: String = "",
    val views: String = "",
    val likes: String = "",
    val uploadTime: String = ""
)

data class Channel(
    val channelName: String = "",
    val channelAvatar: String = "",
)