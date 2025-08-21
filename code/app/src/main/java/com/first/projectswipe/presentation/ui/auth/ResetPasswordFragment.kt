//package com.first.projectswipe.presentation.ui.auth
//
//import android.os.Bundle
//import android.util.Log
//import android.util.Patterns
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.widget.doOnTextChanged
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.first.projectswipe.R
//import com.first.projectswipe.databinding.FragmentResetPasswordBinding
//import com.google.firebase.auth.FirebaseAuth
//
//class ResetPasswordFragment : Fragment() {
//
//    private var _binding: FragmentResetPasswordBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var auth: FirebaseAuth
//    private val TAG = "ResetPasswordFragment"
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        try {
//            setupFirebaseAuth()
//            setupUI()
//            setupClickListeners()
//            setupTextWatchers()
//        } catch (e: Exception) {
//            Log.e(TAG, "Error in ResetPasswordFragment initialization: ${e.message}", e)
//            showToast("Reset password initialization failed: ${e.message}")
//        }
//    }
//
//    private fun setupFirebaseAuth() {
//        // Get Firebase Auth instance
//        auth = FirebaseAuth.getInstance()
//        Log.d(TAG, "Firebase Auth instance obtained successfully")
//    }
//
//    private fun setupUI() {
//        // Initially disable send reset link button
//        updateSendButtonState()
//    }
//
//    private fun setupClickListeners() {
//        binding.sendResetLinkButton.setOnClickListener {
//            val email = binding.emailInput.text.toString().trim()
//
//            if (validateEmail(email)) {
//                sendPasswordResetEmail(email)
//            }
//        }
//
//        binding.backToLoginText.setOnClickListener {
//            try {
//                findNavController().popBackStack()
//            } catch (e: Exception) {
//                Log.e(TAG, "Navigation error: ${e.message}", e)
//                // Alternative navigation method
//                findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
//            }
//        }
//    }
//
//    private fun setupTextWatchers() {
//        binding.emailInput.doOnTextChanged { _, _, _, _ ->
//            updateSendButtonState()
//            clearEmailError()
//        }
//    }
//
//    private fun updateSendButtonState() {
//        val email = binding.emailInput.text.toString().trim()
//
//        binding.sendResetLinkButton.isEnabled = email.isNotEmpty()
//
//        // Update button appearance based on state
//        binding.sendResetLinkButton.alpha = if (binding.sendResetLinkButton.isEnabled) 1.0f else 0.6f
//    }
//
//    private fun validateEmail(email: String): Boolean {
//        var isValid = true
//
//        // Validate email
//        if (email.isEmpty()) {
//            binding.emailLayout.error = "Email is required"
//            isValid = false
//        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            binding.emailLayout.error = "Please enter a valid email address"
//            isValid = false
//        }
//        return isValid
//    }
//
//    private fun clearEmailError() {
//        binding.emailLayout.error = null
//    }
//
//    private fun sendPasswordResetEmail(email: String) {
//        showLoading(true)
//
//        auth.sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                showLoading(false)
//
//                if (task.isSuccessful) {
//                    Log.d(TAG, "Password reset email sent successfully")
//                    showSuccessDialog(email)
//                } else {
//                    Log.w(TAG, "Failed to send password reset email", task.exception)
//                    val errorMessage = when (task.exception?.message) {
//                        "There is no user record corresponding to this identifier. The user may have been deleted." ->
//                            "No account found with this email address"
//                        "The email address is badly formatted." ->
//                            "Please enter a valid email address"
//                        else ->
//                            "Failed to send reset email. Please try again."
//                    }
//                    showToast(errorMessage)
//                }
//            }
//    }
//
//    private fun showSuccessDialog(email: String) {
//        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
//            .setTitle("Reset Link Sent")
//            .setMessage("We've sent a password reset link to $email. Please check your email and follow the instructions to reset your password.")
//            .setPositiveButton("OK") { _, _ ->
//                // Navigate back to login
//                try {
//                    findNavController().popBackStack()
//                } catch (e: Exception) {
//                    Log.e(TAG, "Navigation error from dialog: ${e.message}", e)
//                    findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
//                }
//            }
//            .setCancelable(false)
//            .create()
//
//        dialog.show()
//    }
//
//    private fun showLoading(isLoading: Boolean) {
//        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
//        binding.sendResetLinkButton.isEnabled = !isLoading
//        binding.emailInput.isEnabled = !isLoading
//        binding.backToLoginText.isEnabled = !isLoading
//
//        // Update button appearance when loading
//        if (isLoading) {
//            binding.sendResetLinkButton.alpha = 0.6f
//        } else {
//            updateSendButtonState()
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}