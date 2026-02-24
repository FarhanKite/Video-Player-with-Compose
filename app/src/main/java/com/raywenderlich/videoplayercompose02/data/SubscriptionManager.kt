package com.raywenderlich.videoplayercompose02.data

import androidx.compose.runtime.mutableStateListOf

object SubscriptionManager {

    private val subscribedChannels = mutableStateListOf<Channel>()

    fun subscribe(channel: Channel) {
        if (!subscribedChannels.any { it.channelName == channel.channelName }) {
            subscribedChannels.add(channel)
        }
    }

    fun unsubscribe(channelName: String) {
        subscribedChannels.removeAll { it.channelName == channelName }
    }

    fun isSubscribed(channelName: String): Boolean {
        return subscribedChannels.any { it.channelName == channelName }
    }

    fun getSubscribedChannels(): List<Channel> {
        return subscribedChannels.toList()
    }

    fun getSubscribedChannelNames(): List<String> {
        return subscribedChannels.map { it.channelName }
    }

    fun getSubscriptionCount(): Int {
        return subscribedChannels.size
    }

    fun clearAll() {
        subscribedChannels.clear()
    }
}