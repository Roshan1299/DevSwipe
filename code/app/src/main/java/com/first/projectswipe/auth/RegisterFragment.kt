package com.first.projectswipe.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        setupUniversityDropdown()
        setupClickListeners()
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
                showLoading(true)
                registerWithFirebase(fullName, email, password, university)
            }
        }

        binding.loginPrompt.setOnClickListener {
            // Navigate to login screen - adjust the action based on your navigation graph
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        university: String
    ): Boolean {

        // Clear any previous errors
        binding.fullNameLayout.error = null
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        binding.confirmPasswordLayout.error = null
        binding.universityLayout.error = null

        when {
            fullName.isEmpty() -> {
                binding.fullNameLayout.error = "Full name is required"
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

            password.length < 6 -> {
                binding.passwordLayout.error = "Password must be at least 6 characters"
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
    }

    private fun registerWithFirebase(fullName: String, email: String, password: String, university: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        saveUserProfileToFirestore(uid, fullName, email, university)
                    } else {
                        showLoading(false)
                        showError("Registration failed: User ID not found")
                    }
                } else {
                    showLoading(false)
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    showError(errorMessage)
                }
            }
    }

    private fun saveUserProfileToFirestore(uid: String, fullName: String, email: String, university: String) {
        val user = hashMapOf(
            "name" to fullName,
            "email" to email,
            "university" to university,
            "bio" to "",
            "skills" to listOf<String>(),
            "interests" to listOf<String>(),
            "profileImageUrl" to "",
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                showLoading(false)
                showSuccess("Account created successfully!")

                // Navigate to onboarding
                findNavController().navigate(R.id.action_registerFragment_to_onboardingSkillsFragment)
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showError("Failed to save profile: ${exception.message}")

                // Delete the auth user if Firestore fails
                auth.currentUser?.delete()
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading

        // Disable all input fields during loading
        binding.fullNameInput.isEnabled = !isLoading
        binding.emailInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading
        binding.confirmPasswordInput.isEnabled = !isLoading
        binding.universityInput.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}