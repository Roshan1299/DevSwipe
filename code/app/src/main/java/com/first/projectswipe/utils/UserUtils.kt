package com.first.projectswipe.utils

import com.google.firebase.firestore.FirebaseFirestore

object UserUtils {
    fun getUserNameFromUid(uid: String, callback: (String) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc?.getString("name") ?: "Unknown"
                callback(name)
            }
            .addOnFailureListener {
                callback("Unknown")
            }
    }
}
