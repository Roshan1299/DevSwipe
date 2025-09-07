
package com.first.projectswipe.presentation.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.UpdateUserRequest
import com.first.projectswipe.network.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserDto>()
    val userProfile: LiveData<UserDto> = _userProfile

    private val _uploadUrl = MutableLiveData<String>()
    val uploadUrl: LiveData<String> = _uploadUrl

    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getUserProfile(userId)
                if (response.isSuccessful) {
                    _userProfile.postValue(response.body())
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateUser(userId: String, updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                apiService.updateUser(userId, updateUserRequest)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun uploadProfilePicture(imageUri: Uri, file: File) {
        viewModelScope.launch {
            try {
                val requestFile = file.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val response = apiService.uploadFile(body)
                if (response.isSuccessful) {
                    _uploadUrl.postValue(response.body()?.get("url"))
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
