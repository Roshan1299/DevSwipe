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

    data class UserInfo(val name: String, val profileImageUrl: String?)

    fun getUserInfo(uid: String, callback: (UserInfo) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc?.getString("name") ?: "Unknown"
                val profileImageUrl = doc?.getString("profileImageUrl")
                callback(UserInfo(name, profileImageUrl))
            }
            .addOnFailureListener {
                callback(UserInfo("Unknown", null))
            }
    }
}
