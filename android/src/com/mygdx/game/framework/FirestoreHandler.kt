package com.mygdx.game.framework

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.mygdx.game.data.dao.Player


fun postPlayerToFirestore(firestore: FirebaseFirestore, player: Player): Boolean {
    return try {
        val documentRef = firestore.collection("players").document(player.username)
        documentRef.set(mapOf("username" to player.username,
            "location" to GeoPoint(player.location.latitude,player.location.longitude),

        ))// Update the document with player data
        true
    } catch (e: FirebaseFirestoreException) {
        // Handle Firestore exceptions
        if (e.code == FirebaseFirestoreException.Code.UNKNOWN) {
            // The exception is due to an unknown error, which might be a 500 error
            // Handle the 500 error here or return false
            Log.d("AuthManager", "Failed to post player in Firestore cause: ${e.message},${player.username}")
            false
        } else {
            // Handle other Firestore-specific errors
            // For example, log the error
            Log.d("AuthManager", "Failed to post player in Firestore cause: ${e.message},${player.username}")
            false
        }
    }catch (e :Exception){
        Log.d("AuthManager", "Failed to post player in Firestore cause: ${e.message},${player.username}")
        false
    }
}


 fun updatePlayerFirestore(firestore: FirebaseFirestore, name: String, newName: String): String {
    val playerDocument = firestore.collection("players").document(name)
    println(name+newName)
    return try {
        // Update the existing document with the new name
        playerDocument.update("username", newName)
        newName // Return the new name
    } catch (e: FirebaseFirestoreException) {
        // Handle Firestore exceptions
        if (e.code == FirebaseFirestoreException.Code.UNKNOWN) {
            // The exception is due to an unknown error, which might be a 500 error
            // Handle the 500 error here or return an empty string
            Log.d("AuthManager", "Failed to update player in Firestore cause: ${e.message}")
            ""
        } else {
            // Handle other Firestore-specific errors
            // For example, log the error
            Log.d("AuthManager", "Firestore Error, failed to update player in Firestore cause: ${e.message}")
            ""
        }
    }catch (e :Exception){
        Log.d("AuthManager", "Firestore Error, failed to update player in Firestore cause: ${e.message}")
        ""
    }
     }

