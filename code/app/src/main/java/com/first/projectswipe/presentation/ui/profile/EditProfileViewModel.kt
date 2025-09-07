
package com.first.projectswipe.presentation.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.UpdateUserRequest
import com.first.projectswipe.network.dto.UserProfileResponse
import com.first.projectswipe.network.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfileResponse>()
    val userProfile: LiveData<UserProfileResponse> = _userProfile

    private val _uploadUrl = MutableLiveData<String>()
    val uploadUrl: LiveData<String> = _uploadUrl

    fun getUserProfile() {
        viewModelScope.launch {
            try {
                val response = apiService.getCurrentProfile()
                if (response.isSuccessful) {
                    _userProfile.postValue(response.body())
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateUser(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                apiService.createOrUpdateProfile(updateUserRequest)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun uploadProfilePicture(imageUri: Uri, file: File) {
        viewModelScope.launch {
            try {
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
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
