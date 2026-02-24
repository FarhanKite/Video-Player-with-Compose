package com.raywenderlich.videoplayercompose02.data

sealed class HomeItem {
    data class VideoItem(val video: Video) : HomeItem()
    data class ShortsGridItem(val shorts: List<Short>) : HomeItem()
}