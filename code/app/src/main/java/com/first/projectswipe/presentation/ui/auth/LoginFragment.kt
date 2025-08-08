package com.first.projectswipe.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "LoginFragment"

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupFirebaseAuth()
            setupUI()
            setupClickListeners()
            setupTextWatchers()
        } catch (e: Exception) {
            Log.e(TAG, "Error in LoginFragment initialization: ${e.message}", e)
            showToast("Login initialization failed: ${e.message}")
        }
    }

    private fun setupFirebaseAuth() {
        // Get Firebase Auth instance
        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "Firebase Auth instance obtained successfully")

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun setupUI() {
        // Initially disable login button
        updateLoginButtonState()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()

            if (validateInputs(email, password)) {
                loginWithFirebase(email, password)
            }
        }

        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }

        binding.registerPrompt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun setupTextWatchers() {
        binding.emailInput.doOnTextChanged { _, _, _, _ ->
            updateLoginButtonState()
            clearEmailError()
        }

        binding.passwordInput.doOnTextChanged { _, _, _, _ ->
            updateLoginButtonState()
            clearPasswordError()
        }
    }

    private fun updateLoginButtonState() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        binding.loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()

        // Update button appearance based on state
        binding.loginButton.alpha = if (binding.loginButton.isEnabled) 1.0f else 0.6f
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        // Validate email
        if (email.isEmpty()) {
            binding.emailLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Please enter a valid email address"
            isValid = false
        }

        // Validate password
        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun clearEmailError() {
        binding.emailLayout.error = null
    }

    private fun clearPasswordError() {
        binding.passwordLayout.error = null
    }

    private fun loginWithFirebase(email: String, password: String) {
        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    showToast("Welcome back, ${user?.email}")

                    // Check if user needs onboarding
                    checkOnboardingStatus()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    val errorMessage = when (task.exception?.message) {
                        "The email address is badly formatted." -> "Please enter a valid email address"
                        "There is no user record corresponding to this identifier. The user may have been deleted." -> "No account found with this email"
                        "The password is invalid or the user does not have a password." -> "Incorrect password"
                        else -> "Login failed. Please try again."
                    }
                    showToast(errorMessage)
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                account?.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                showToast("Google Sign-In failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoading(true)

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    showToast("Welcome, ${user?.displayName}")

                    // Check if user needs onboarding
                    checkOnboardingStatus()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showToast("Authentication failed")
                }
            }
    }

    private fun checkOnboardingStatus() {
        val currentUser = auth.currentUser ?: return

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val userDoc = db.collection("users").document(currentUser.uid).get().await()

                if (userDoc.exists()) {
                    val onboardingCompleted = userDoc.getBoolean("onboardingCompleted") ?: false

                    if (onboardingCompleted) {
                        // User has completed onboarding, go to main app
                        findNavController().navigate(R.id.action_loginFragment_to_ideasFragment)
                    } else {
                        // User needs to complete onboarding
                        findNavController().navigate(R.id.action_loginFragment_to_onboardingSkillsFragment)
                    }
                } else {
                    // User document doesn't exist, they might be new - go to onboarding
                    findNavController().navigate(R.id.action_loginFragment_to_onboardingSkillsFragment)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error checking onboarding status", e)
                // On error, default to main app
                findNavController().navigate(R.id.action_loginFragment_to_ideasFragment)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.emailInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in and update UI accordingly
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, check onboarding status
            checkOnboardingStatus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}