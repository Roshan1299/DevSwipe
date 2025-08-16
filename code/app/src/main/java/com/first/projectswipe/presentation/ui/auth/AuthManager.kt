package com.first.projectswipe.presentation.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.first.projectswipe.data.models.User
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthManager private constructor(private val context: Context) {
    private val TAG = "AuthManager"
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private lateinit var apiService: ApiService

    companion object {
        @Volatile
        private var INSTANCE: AuthManager? = null
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_FIRST_NAME = "user_first_name"
        private const val KEY_USER_LAST_NAME = "user_last_name"

        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun initialize(apiService: ApiService) {
        this.apiService = apiService
        // Check if user is already logged in
        val token = getToken()
        if (token != null) {
            loadUserFromPrefs()
            _isLoggedIn.value = true
        } else {
            _isLoggedIn.value = false
        }
    }

    // Authentication Methods
    suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = apiService.login(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    saveUserSession(authResponse)

                    withContext(Dispatchers.Main) {
                        _isLoggedIn.value = true
                    }

                    Log.d(TAG, "Login successful for: $email")
                    AuthResult.Success(authResponse.user)
                } else {
                    Log.e(TAG, "Login failed: ${response.errorBody()?.string()}")
                    AuthResult.Error("Login failed. Please check your credentials.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login error: ${e.message}", e)
                AuthResult.Error("Network error. Please try again.")
            }
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null
    ): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName
                )

                val response = apiService.register(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    saveUserSession(authResponse)

                    withContext(Dispatchers.Main) {
                        _isLoggedIn.value = true
                    }

                    Log.d(TAG, "Registration successful for: $email")
                    AuthResult.Success(authResponse.user)
                } else {
                    Log.e(TAG, "Registration failed: ${response.errorBody()?.string()}")
                    AuthResult.Error("Registration failed. Please try again.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Registration error: ${e.message}", e)
                AuthResult.Error("Network error. Please try again.")
            }
        }
    }

    fun logout() {
        // Clear all stored data
        prefs.edit().clear().apply()

        _currentUser.value = null
        _isLoggedIn.value = false

        Log.d(TAG, "User logged out")
    }

    // Session Management
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getCurrentUser(): User? {
        return _currentUser.value
    }

    fun isUserLoggedIn(): Boolean {
        return getToken() != null && _currentUser.value != null
    }

    // Private Methods
    private fun saveUserSession(authResponse: AuthResponse) {
        val user = authResponse.user

        prefs.edit().apply {
            putString(KEY_TOKEN, authResponse.token)
            putString(KEY_USER_ID, user.id.toString())
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_NAME, user.username)
            putString(KEY_USER_FIRST_NAME, user.firstName ?: "")
            putString(KEY_USER_LAST_NAME, user.lastName ?: "")
            apply()
        }

        val appUser = User(
            id = user.id.toString(),
            name = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim().ifEmpty { user.username },
            email = user.email,
            username = user.username,
            bio = "", // Will be populated when we implement profile features
            skills = emptyList(),
            interests = emptyList(),
            profileImageUrl = "",
            createdAt = System.currentTimeMillis()
        )

        _currentUser.postValue(appUser)
        Log.d(TAG, "User session saved for: ${user.email}")
    }

    private fun loadUserFromPrefs() {
        val userId = prefs.getString(KEY_USER_ID, null)
        val email = prefs.getString(KEY_USER_EMAIL, null)
        val username = prefs.getString(KEY_USER_NAME, null)
        val firstName = prefs.getString(KEY_USER_FIRST_NAME, "")
        val lastName = prefs.getString(KEY_USER_LAST_NAME, "")

        if (userId != null && email != null && username != null) {
            val user = User(
                id = userId,
                name = "$firstName $lastName".trim().ifEmpty { username },
                email = email,
                username = username,
                bio = "",
                skills = emptyList(),
                interests = emptyList(),
                profileImageUrl = "",
                createdAt = System.currentTimeMillis()
            )

            _currentUser.postValue(user)
            Log.d(TAG, "User loaded from preferences: $email")
        }
    }
}

// Result classes
sealed class AuthResult {
    data class Success(val user: UserDto) : AuthResult()
    data class Error(val message: String) : AuthResult()
}