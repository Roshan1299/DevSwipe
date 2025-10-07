package com.first.projectswipe.utils

import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.UserDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

object UserUtils {

    data class UserInfo(val name: String, val profileImageUrl: String?)

    fun getUserInfo(uid: String, apiService: ApiService, callback: (UserInfo) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.getUserProfile(uid)
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    if (userProfile != null) {
                        val userInfo = UserInfo(userProfile.name, userProfile.profileImageUrl)
                        callback(userInfo)
                    } else {
                        callback(UserInfo("Unknown", null))
                    }
                } else {
                    callback(UserInfo("Unknown", null))
                }
            } catch (e: Exception) {
                callback(UserInfo("Unknown", null))
            }
        }
    }
}
