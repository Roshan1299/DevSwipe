package com.first.projectswipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
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
            // Get Firebase Auth instance directly
            // MainActivity should have already initialized Firebase
            auth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Auth instance obtained successfully")

            // Configure Google Sign-In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            // Handle email/password login
            binding.loginButton.setOnClickListener {
                val email = binding.emailInput.text.toString()
                val password = binding.passwordInput.text.toString()

                if (validateInputs(email, password)) {
                    binding.progressIndicator.visibility = View.VISIBLE
                    loginWithFirebase(email, password)
                }
            }

            // Handle Google Sign-In button click
            binding.googleSignInButton.setOnClickListener {
                signInWithGoogle()
            }

            // Navigate to Register screen
            binding.registerPrompt.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            // Navigate to Reset Password screen
            binding.forgotPasswordText.setOnClickListener {
                // Use Navigation Component to navigate to ResetPasswordFragment
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in LoginFragment initialization: ${e.message}", e)
            Toast.makeText(context, "Login initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Rest of the methods remain the same...
    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loginWithFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressIndicator.visibility = View.GONE
                if (task.isSuccessful) {
                    // Navigate to FirstFragment after successful login
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    // Show error message
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
                account?.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                // Handle Google Sign-In failure
                Toast.makeText(context, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.progressIndicator.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                binding.progressIndicator.visibility = View.GONE
                if (task.isSuccessful) {
                    // Navigate to FirstFragment after successful Google Sign-In
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    // Show error message
                    Toast.makeText(context, "Google Sign-In failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}