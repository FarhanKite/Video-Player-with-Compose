package com.raywenderlich.videoplayercompose02.repository

import com.raywenderlich.videoplayercompose02.data.Short
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ShortsRepository {

    private val database = FirebaseDatabase.getInstance()
    private val shortsRef = database.getReference("shorts")

    fun getShorts(): Flow<List<Short>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shortList = mutableListOf<Short>()
                for (shortSnapshot in snapshot.children) {
                    try {
                        val short = shortSnapshot.getValue(Short::class.java)
                        if (short != null) {
                            shortList.add(short)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                trySend(shortList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        shortsRef.addValueEventListener(listener)

        awaitClose {
            shortsRef.removeEventListener(listener)
        }
    }
}