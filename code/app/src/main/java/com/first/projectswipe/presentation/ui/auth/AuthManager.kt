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

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

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
        private const val KEY_USER_SKILLS = "user_skills"
        private const val KEY_USER_INTERESTS = "user_interests"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"

        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
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
                _authState.postValue(AuthState.Loading)

                val loginRequest = LoginRequest(email, password)
                val response = apiService.login(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    saveUserSession(authResponse)

                    withContext(Dispatchers.Main) {
                        _isLoggedIn.value = true
                        _authState.value = AuthState.Success(getCurrentUser()!!)
                    }

                    Log.d(TAG, "Login successful for: $email")
                    AuthResult.Success(authResponse.user)
                } else {
                    _authState.postValue(AuthState.Error("Login failed. Please check your credentials."))
                    Log.e(TAG, "Login failed: ${response.errorBody()?.string()}")
                    AuthResult.Error("Login failed. Please check your credentials.")
                }
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error("Network error. Please try again."))
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
                _authState.postValue(AuthState.Loading)

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
                        _authState.value = AuthState.Success(getCurrentUser()!!)
                    }

                    Log.d(TAG, "Registration successful for: $email")
                    AuthResult.Success(authResponse.user)
                } else {
                    _authState.postValue(AuthState.Error("Registration failed. Please try again."))
                    Log.e(TAG, "Registration failed: ${response.errorBody()?.string()}")
                    AuthResult.Error("Registration failed. Please try again.")
                }
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error("Network error. Please try again."))
                Log.e(TAG, "Registration error: ${e.message}", e)
                AuthResult.Error("Network error. Please try again.")
            }
        }
    }

    // New method: Register with RegisterRequest (for your existing code)
    suspend fun register(registerRequest: RegisterRequest) {
        withContext(Dispatchers.IO) {
            try {
                _authState.postValue(AuthState.Loading)

                val response = apiService.register(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    saveUserSession(authResponse)

                    withContext(Dispatchers.Main) {
                        _isLoggedIn.value = true
                        _authState.value = AuthState.Success(getCurrentUser()!!)
                    }

                    Log.d(TAG, "Registration successful for: ${registerRequest.email}")
                } else {
                    _authState.postValue(AuthState.Error("Registration failed. Please try again."))
                    Log.e(TAG, "Registration failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error("Network error. Please try again."))
                Log.e(TAG, "Registration error: ${e.message}", e)
            }
        }
    }

    // Onboarding Methods
    suspend fun updateUserSkills(skills: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = getCurrentUser() ?: return@withContext false

                val skillsRequest = UpdateSkillsRequest(skills = skills)
                val response = apiService.updateUserSkills(currentUser.id, skillsRequest)

                if (response.isSuccessful && response.body() != null) {
                    val updatedUserDto = response.body()!!
                    updateLocalUserData(skills = skills)
                    Log.d(TAG, "Skills updated successfully")
                    true
                } else {
                    Log.e(TAG, "Failed to update skills: ${response.errorBody()?.string()}")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating skills: ${e.message}", e)
                false
            }
        }
    }

    suspend fun updateUserInterestsAndCompleteOnboarding(interests: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = getCurrentUser() ?: return@withContext false

                val interestsRequest = UpdateInterestsRequest(interests = interests)
                val response = apiService.updateUserInterests(currentUser.id, interestsRequest)

                if (response.isSuccessful && response.body() != null) {
                    updateLocalUserData(interests = interests)

                    // Now complete onboarding
                    val onboardingResponse = apiService.completeOnboarding(currentUser.id)
                    if (onboardingResponse.isSuccessful) {
                        markOnboardingComplete()
                        Log.d(TAG, "Interests updated and onboarding completed successfully")
                        true
                    } else {
                        Log.e(TAG, "Failed to complete onboarding: ${onboardingResponse.errorBody()?.string()}")
                        false
                    }
                } else {
                    Log.e(TAG, "Failed to update interests: ${response.errorBody()?.string()}")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating interests and completing onboarding: ${e.message}", e)
                false
            }
        }
    }

    suspend fun completeOnboarding(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = getCurrentUser() ?: return@withContext false

                val response = apiService.completeOnboarding(currentUser.id)

                if (response.isSuccessful) {
                    markOnboardingComplete()
                    Log.d(TAG, "Onboarding completed successfully")
                    true
                } else {
                    Log.e(TAG, "Failed to complete onboarding: ${response.errorBody()?.string()}")
                    // Still mark locally as complete
                    markOnboardingComplete()
                    true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error completing onboarding: ${e.message}", e)
                // Still mark locally as complete
                markOnboardingComplete()
                true
            }
        }
    }

    fun logout() {
        // Clear all stored data
        prefs.edit().clear().apply()

        _currentUser.value = null
        _isLoggedIn.value = false
        _authState.value = AuthState.Idle

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

    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
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

            // Save skills and interests if available
            user.skills?.let { skills ->
                putString(KEY_USER_SKILLS, skills.joinToString(","))
            }
            user.interests?.let { interests ->
                putString(KEY_USER_INTERESTS, interests.joinToString(","))
            }

            // Save onboarding status
            putBoolean(KEY_ONBOARDING_COMPLETE, user.onboardingCompleted ?: false)

            apply()
        }

        val appUser = User(
            id = user.id.toString(),
            name = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim().ifEmpty { user.username },
            email = user.email,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            bio = user.bio ?: "",
            skills = user.skills ?: emptyList(),
            interests = user.interests ?: emptyList(),
            onboardingCompleted = user.onboardingCompleted ?: false,
            profileImageUrl = user.profileImageUrl ?: "",
            createdAt = user.createdAt ?: System.currentTimeMillis()
        )

        _currentUser.postValue(appUser)
        Log.d(TAG, "User session saved for: ${user.email}")
    }

    private fun loadUserFromPrefs() {
        val userId = prefs.getString(KEY_USER_ID, null)
        val email = prefs.getString(KEY_USER_EMAIL, null)
        val username = prefs.getString(KEY_USER_NAME, null)
        val firstName = prefs.getString(KEY_USER_FIRST_NAME, "") ?: ""
        val lastName = prefs.getString(KEY_USER_LAST_NAME, "") ?: ""
        val skillsString = prefs.getString(KEY_USER_SKILLS, null)
        val interestsString = prefs.getString(KEY_USER_INTERESTS, null)
        val onboardingComplete = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)

        if (userId != null && email != null && username != null) {
            val skills = skillsString?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            val interests = interestsString?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

            val user = User(
                id = userId,
                name = "$firstName $lastName".trim().ifEmpty { username },
                email = email,
                username = username,
                firstName = firstName.takeIf { it.isNotEmpty() },
                lastName = lastName.takeIf { it.isNotEmpty() },
                bio = "",
                skills = skills,
                interests = interests,
                onboardingCompleted = onboardingComplete,
                profileImageUrl = "",
                createdAt = System.currentTimeMillis()
            )

            _currentUser.postValue(user)
            Log.d(TAG, "User loaded from preferences: $email")
        }
    }

    private fun updateLocalUserData(skills: List<String>? = null, interests: List<String>? = null) {
        val currentUser = getCurrentUser() ?: return

        val updatedUser = currentUser.copy(
            skills = skills ?: currentUser.skills,
            interests = interests ?: currentUser.interests
        )

        // Save to preferences
        prefs.edit().apply {
            skills?.let {
                putString(KEY_USER_SKILLS, it.joinToString(","))
            }
            interests?.let {
                putString(KEY_USER_INTERESTS, it.joinToString(","))
            }
            apply()
        }

        _currentUser.postValue(updatedUser)
    }

    private fun markOnboardingComplete() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()

        val currentUser = getCurrentUser()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(onboardingCompleted = true)
            _currentUser.postValue(updatedUser)
        }
    }
}

// Result classes
//sealed class AuthResult {
//    data class Success(val user: UserDto) : AuthResult()
//    data class Error(val message: String) : AuthResult()
//}