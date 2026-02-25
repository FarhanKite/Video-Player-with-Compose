package com.raywenderlich.videoplayercompose02.repository

import com.raywenderlich.videoplayercompose02.data.Channel
import com.raywenderlich.videoplayercompose02.data.Video
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SubscriptionRepository {

    private val database = FirebaseDatabase.getInstance()

    fun observeChannels(): Flow<List<Channel>> = callbackFlow {
        val ref = database.getReference("channels")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val channelList = mutableListOf<Channel>()
                for (channelSnapshot in snapshot.children) {
                    val channel = channelSnapshot.getValue(Channel::class.java)
                    if (channel != null) {
                        channelList.add(channel)
                    }
                }
                trySend(channelList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeVideos(): Flow<List<Video>> = callbackFlow {
        val ref = database.getReference("videos")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val videoList = mutableListOf<Video>()
                for (videoSnapshot in snapshot.children) {
                    val video = videoSnapshot.getValue(Video::class.java)
                    if (video != null) {
                        videoList.add(video)
                    }
                }
                trySend(videoList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}