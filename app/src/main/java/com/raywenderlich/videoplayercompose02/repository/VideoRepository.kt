package com.raywenderlich.videoplayercompose02.repository

import com.raywenderlich.videoplayercompose02.data.Short
import com.raywenderlich.videoplayercompose02.data.Video
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class VideoRepository {

    private val database = FirebaseDatabase.getInstance()

    fun observeVideos(): Flow<List<Video>> = callbackFlow {
        val ref = database.getReference("videos")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Video>()
                for (child in snapshot.children) {
                    try {
                        val video = child.getValue(Video::class.java)
                        if (video != null) list.add(video)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeShorts(): Flow<List<Short>> = callbackFlow {
        val ref = database.getReference("shorts")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Short>()
                for (child in snapshot.children) {
                    try {
                        val short = child.getValue(Short::class.java)
                        if (short != null) list.add(short)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}