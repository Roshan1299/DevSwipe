package com.first.projectswipe.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

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

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameInput.text.toString()
            val lastName = binding.lastNameInput.text.toString()
            val username = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            if (validateInputs(firstName, lastName, username, email, password, confirmPassword)) {
                binding.progressIndicator.visibility = View.VISIBLE
                registerWithFirebase(email, password)
            }
        }

        binding.loginPrompt.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_ideasFragment)
        }
    }

    private fun validateInputs(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
            email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registerWithFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressIndicator.visibility = View.GONE
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        saveUserProfileToFirestore(uid)
                    }

                    Toast.makeText(
                        context,
                        "Registration successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    Toast.makeText(
                        context,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun saveUserProfileToFirestore(uid: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        val user = hashMapOf(
            "name" to "${binding.firstNameInput.text} ${binding.lastNameInput.text}",
            "username" to binding.usernameInput.text.toString(),
            "university" to "", // You can add a field in the form later
            "bio" to "",         // Optional: let user update in profile screen
            "skills" to listOf<String>(),     // empty for now
            "interests" to listOf<String>(), // empty for now
            "profileImageUrl" to ""          // optional
        )

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile created!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save user profile", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}