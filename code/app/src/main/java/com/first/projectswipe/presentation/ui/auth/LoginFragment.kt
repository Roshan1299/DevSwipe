package com.first.projectswipe.presentation.ui.auth

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
import com.first.projectswipe.presentation.ui.auth.AuthManager
import com.first.projectswipe.presentation.ui.auth.AuthResult
import com.first.projectswipe.databinding.FragmentLoginBinding
import com.first.projectswipe.network.NetworkModule
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager
    private val TAG = "LoginFragment"

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
            setupAuth()
            setupUI()
            setupClickListeners()
            setupTextWatchers()
            observeAuthState()
        } catch (e: Exception) {
            Log.e(TAG, "Error in LoginFragment initialization: ${e.message}", e)
            showToast("Login initialization failed: ${e.message}")
        }
    }

    private fun setupAuth() {
        authManager = AuthManager.getInstance(requireContext())
        val apiService = NetworkModule.provideApiService(requireContext())
        authManager.initialize(apiService)
        Log.d(TAG, "Auth manager initialized successfully")
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
                loginUser(email, password)
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

    private fun observeAuthState() {
        authManager.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                // Navigate to home screen
                try {
                    // For now, navigate directly to home. Later we can add onboarding check
                    findNavController().navigate(R.id.action_loginFragment_to_ideasFragment)
                } catch (e: Exception) {
                    Log.e(TAG, "Navigation error: ${e.message}", e)
                    // Fallback navigation
                    findNavController().navigate(R.id.homeFragment)
                }
            }
        }
    }

    private fun updateLoginButtonState() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        binding.loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
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

    private fun loginUser(email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                when (val result = authManager.login(email, password)) {
                    is AuthResult.Success -> {
                        // Check if fragment is still attached before updating UI
                        if (isAdded && _binding != null) {
                            showLoading(false)
                            Log.d(TAG, "Login successful")
                            showToast("Welcome back!")
                        }
                        // Navigation will be handled by observeAuthState()
                    }
                    is AuthResult.Error -> {
                        // Check if fragment is still attached before updating UI
                        if (isAdded && _binding != null) {
                            showLoading(false)
                            Log.e(TAG, "Login failed: ${result.message}")
                            showToast(result.message)
                        }
                    }
                }
            } catch (e: Exception) {
                // Check if fragment is still attached before updating UI
                if (isAdded && _binding != null) {
                    showLoading(false)
                    Log.e(TAG, "Login error: ${e.message}", e)
                    showToast("Login failed. Please try again.")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Check if binding is available before using it
        if (_binding == null) return

        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.emailInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading

        // Update button appearance when loading
        if (isLoading) {
            binding.loginButton.alpha = 0.6f
        } else {
            updateLoginButtonState()
        }
    }

    private fun showToast(message: String) {
        // Only show toast if context is available
        context?.let { ctx ->
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already signed in
        if (authManager.isUserLoggedIn()) {
            Log.d(TAG, "User already logged in, navigating to home")
            // Navigation will be handled by observeAuthState()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}