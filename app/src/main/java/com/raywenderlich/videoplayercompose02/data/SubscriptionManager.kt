//package com.raywenderlich.videoplayercompose02.data
//
//import androidx.compose.runtime.mutableStateListOf
//
//object SubscriptionManager {
//
//    private val subscribedChannelIds = mutableStateListOf<String>()
//
//    fun subscribe(channelId: String) {
//        // Check: Are we already subscribed?
//        if (!subscribedChannelIds.contains(channelId)) {
//            // Not subscribed yet, so add it!
//            subscribedChannelIds.add(channelId)
//            println("Subscribed to: $channelId")
//        } else {
//            println("Already subscribed to: $channelId")
//        }
//    }
//
//    fun unsubscribe(channelId: String) {
//        subscribedChannelIds.remove(channelId)
//        println("Unsubscribed from: $channelId")
//    }
//
//    fun isSubscribed(channelId: String): Boolean {
//        return subscribedChannelIds.contains(channelId)
//    }
//
//    fun getSubscribedChannels(): List<String> {
//        return subscribedChannelIds.toList()
//    }
//
//    fun getSubscriptionCount(): Int {
//        return subscribedChannelIds.size
//    }
//
//    fun clearAll() {
//        subscribedChannelIds.clear()
//        println("All subscriptions cleared")
//    }
//}



//
//package com.raywenderlich.videoplayercompose02.data
//
//import androidx.compose.runtime.mutableStateListOf
//
//object SubscriptionManager {
//
//    private val subscribedChannelNames = mutableStateListOf<String>()
//
//    fun subscribe(channelName: String) {
//        if (!subscribedChannelNames.contains(channelName)) {
//            subscribedChannelNames.add(channelName)
//            println("✅ Subscribed to: $channelName")
//        }
//    }
//
//    fun unsubscribe(channelName: String) {
//        subscribedChannelNames.remove(channelName)
//        println("❌ Unsubscribed from: $channelName")
//    }
//
//    fun isSubscribed(channelName: String): Boolean {
//        return subscribedChannelNames.contains(channelName)
//    }
//
//    fun getSubscribedChannels(): List<String> {
//        return subscribedChannelNames.toList()
//    }
//
//    fun getSubscriptionCount(): Int {
//        return subscribedChannelNames.size
//    }
//
//    fun clearAll() {
//        subscribedChannelNames.clear()
//    }
//}





package com.raywenderlich.videoplayercompose02.data

import androidx.compose.runtime.mutableStateListOf

object SubscriptionManager {

    private val subscribedChannels = mutableStateListOf<Channel>()

    fun subscribe(channel: Channel) {
        if (!subscribedChannels.any { it.channelName == channel.channelName }) {
            subscribedChannels.add(channel)
            println("✅ Subscribed to: ${channel.channelName}")
        }
    }

    fun unsubscribe(channelName: String) {
        subscribedChannels.removeAll { it.channelName == channelName }
        println("❌ Unsubscribed from: $channelName")
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
        println("🗑️ All subscriptions cleared")
    }
}