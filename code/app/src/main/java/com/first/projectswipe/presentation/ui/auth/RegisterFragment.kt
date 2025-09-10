package com.first.projectswipe.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.databinding.FragmentRegisterBinding
import com.first.projectswipe.network.ApiService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authManager: AuthManager
    
    @Inject
    lateinit var apiService: ApiService

    private val TAG = "RegisterFragment"

    // List of universities - you can expand this
    private val universities = arrayOf(
        "University of Alberta",
        "University of Calgary",
        "University of Toronto",
        "University of British Columbia",
        "McGill University",
        "University of Waterloo",
        "York University",
        "Simon Fraser University",
        "McMaster University",
        "Queen's University",
        "Other"
    )

    // Map of university names to their accepted email domains
    private val universityDomains = mapOf(
        "University of Alberta" to listOf("ualberta.ca"),
        "University of Calgary" to listOf("ucalgary.ca"),
        "University of Toronto" to listOf("mail.utoronto.ca", "utoronto.ca"),
        "University of British Columbia" to listOf("ubc.ca"),
        "McGill University" to listOf("mcgill.ca"),
        "University of Waterloo" to listOf("uwaterloo.ca"),
        "York University" to listOf("yorku.ca"),
        "Simon Fraser University" to listOf("sfu.ca"),
        "McMaster University" to listOf("mcmaster.ca"),
        "Queen's University" to listOf("queensu.ca")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUniversityDropdown()
        setupClickListeners()
        setupTextWatchers()
        observeAuthState()
    }

    private fun setupUniversityDropdown() {
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, universities)
        (binding.universityInput as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val fullName = binding.fullNameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()
            val university = binding.universityInput.text.toString().trim()

            if (validateInputs(fullName, email, password, confirmPassword, university)) {
                registerUser(fullName, email, password, university)
            }
        }

        binding.loginPrompt.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setupTextWatchers() {
        binding.fullNameInput.doOnTextChanged { _, _, _, _ ->
            updateRegisterButtonState()
            clearFullNameError()
        }

        binding.emailInput.doOnTextChanged { _, _, _, _ ->
            updateRegisterButtonState()
            clearEmailError()
        }

        binding.passwordInput.doOnTextChanged { _, _, _, _ ->
            updateRegisterButtonState()
            clearPasswordError()
        }

        binding.confirmPasswordInput.doOnTextChanged { _, _, _, _ ->
            updateRegisterButtonState()
            clearConfirmPasswordError()
        }

        binding.universityInput.doOnTextChanged { _, _, _, _ ->
            updateRegisterButtonState()
            clearUniversityError()
        }
    }

    private fun observeAuthState() {
        authManager.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                // Navigate to onboarding (skills selection)
                try {
                    findNavController().navigate(R.id.action_registerFragment_to_onboardingSkillsFragment)
                } catch (e: Exception) {
                    Log.e(TAG, "Navigation error: ${e.message}", e)
                    // Fallback to home
                    findNavController().navigate(R.id.homeFragment)
                }
            }
        }
    }

    private fun updateRegisterButtonState() {
        val fullName = binding.fullNameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()
        val university = binding.universityInput.text.toString().trim()

        val isEnabled = fullName.isNotEmpty() && email.isNotEmpty() &&
                password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                university.isNotEmpty()

        binding.registerButton.isEnabled = isEnabled
        binding.registerButton.alpha = if (isEnabled) 1.0f else 0.6f
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        university: String
    ): Boolean {

        // Clear any previous errors
        clearAllErrors()

        when {
            fullName.isEmpty() -> {
                binding.fullNameLayout.error = "Full name is required"
                return false
            }

            fullName.length < 2 -> {
                binding.fullNameLayout.error = "Full name must be at least 2 characters"
                return false
            }

            email.isEmpty() -> {
                binding.emailLayout.error = "Email is required"
                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailLayout.error = "Please enter a valid email"
                return false
            }

            password.isEmpty() -> {
                binding.passwordLayout.error = "Password is required"
                return false
            }

            password.length < 8 -> {
                binding.passwordLayout.error = "Password must be at least 8 characters"
                return false
            }

            confirmPassword.isEmpty() -> {
                binding.confirmPasswordLayout.error = "Please confirm your password"
                return false
            }

            password != confirmPassword -> {
                binding.confirmPasswordLayout.error = "Passwords do not match"
                return false
            }

            university.isEmpty() -> {
                binding.universityLayout.error = "Please select your university"
                return false
            }

            !isValidUniversityEmail(email, university) -> {
                val expectedDomains = universityDomains[university]
                val domainsText = if (expectedDomains != null && expectedDomains.size == 1) {
                    "@${expectedDomains[0]}"
                } else if (expectedDomains != null) {
                    expectedDomains.joinToString(" or ") { "@$it" }
                } else {
                    "a valid university email"
                }

                if (university == "Other") {
                    binding.emailLayout.error = "Please use your university email address"
                } else {
                    binding.emailLayout.error = "Please use your $university email ($domainsText)"
                }
                return false
            }

            else -> return true
        }
    }

    private fun isValidUniversityEmail(email: String, university: String): Boolean {
        // For demonstration, we'll skip strict university email validation
        // You can enable this if needed
        return true

        // Uncomment below for strict validation:
        /*
        // If "Other" is selected, check for general university email patterns
        if (university == "Other") {
            return email.contains(".edu") ||
                    email.contains("university") ||
                    email.contains("college") ||
                    email.contains("ac.") ||
                    email.contains(".edu.") ||
                    email.endsWith(".ca") && (email.contains("u") || email.contains("college"))
        }

        // For specific universities, check against their domains
        val acceptedDomains = universityDomains[university] ?: return false
        return acceptedDomains.any { domain ->
            email.endsWith("@$domain")
        }
        */
    }

    private fun registerUser(
        fullName: String,
        email: String,
        password: String,
        university: String
    ) {
        showLoading(true)

        // Extract first and last name
        val nameParts = fullName.split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""

        // Create username from email (part before @)
        val username = email.substringBefore("@")

        lifecycleScope.launch {
            try {
                when (val result = authManager.register(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName.ifEmpty { null },
                    lastName = lastName.ifEmpty { null },
                    university = university
                )) {
                    is AuthResult.Success -> {
                        // Check if fragment is still attached before updating UI
                        if (isAdded && _binding != null) {
                            showLoading(false)
                            Log.d(TAG, "Registration successful")
                            showToast("Account created successfully!")
                        }
                        // Navigation will be handled by observeAuthState()
                    }

                    is AuthResult.Error -> {
                        // Check if fragment is still attached before updating UI
                        if (isAdded && _binding != null) {
                            showLoading(false)
                            Log.e(TAG, "Registration failed: ${result.message}")
                            showToast(result.message)
                        }
                    }
                }
            } catch (e: Exception) {
                // Check if fragment is still attached before updating UI
                if (isAdded && _binding != null) {
                    showLoading(false)
                    Log.e(TAG, "Registration error: ${e.message}", e)
                    showToast("Registration failed. Please try again.")
                }
            }
        }
    }

    private fun clearAllErrors() {
        binding.fullNameLayout.error = null
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        binding.confirmPasswordLayout.error = null
        binding.universityLayout.error = null
    }

    private fun clearFullNameError() {
        binding.fullNameLayout.error = null
    }

    private fun clearEmailError() {
        binding.emailLayout.error = null
    }

    private fun clearPasswordError() {
        binding.passwordLayout.error = null
    }

    private fun clearConfirmPasswordError() {
        binding.confirmPasswordLayout.error = null
    }

    private fun clearUniversityError() {
        binding.universityLayout.error = null
    }

    private fun showLoading(isLoading: Boolean) {
        // Check if binding is available before using it
        if (_binding == null) return

        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading

        // Disable all input fields during loading
        binding.fullNameInput.isEnabled = !isLoading
        binding.emailInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading
        binding.confirmPasswordInput.isEnabled = !isLoading
        binding.universityInput.isEnabled = !isLoading

        // Update button appearance when loading
        if (isLoading) {
            binding.registerButton.alpha = 0.6f
        } else {
            updateRegisterButtonState()
        }
    }

    private fun showToast(message: String) {
        // Only show toast if context is available
        context?.let { ctx ->
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}