//package com.first.projectswipe.presentation.ui.onboarding
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import com.first.projectswipe.R
//import com.google.android.flexbox.FlexboxLayout
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//class OnboardingInterestsFragment : Fragment() {
//
//    private lateinit var stepIndicator: TextView
//    private lateinit var titleText: TextView
//    private lateinit var subtitleText: TextView
//    private lateinit var interestsContainer: FlexboxLayout
//    private lateinit var finishButton: Button
//    private lateinit var skipButton: TextView
//    private lateinit var progressBar: ProgressBar
//
//    private val db = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//
//    private val selectedInterests = mutableListOf<String>()
//    private val maxInterests = 8
//    private val minInterests = 2
//
//    // Comprehensive interests list organized by category
//    private val interestsCategories = mapOf(
//        "Project Types" to listOf(
//            "Web Applications", "Mobile Apps", "Games", "Desktop Software", "APIs", "Chrome Extensions"
//        ),
//        "Technologies" to listOf(
//            "Artificial Intelligence", "Machine Learning", "Blockchain", "IoT", "AR/VR", "Robotics", "Cybersecurity"
//        ),
//        "Domains" to listOf(
//            "E-commerce", "FinTech", "HealthTech", "EdTech", "Social Media", "Productivity Tools", "Entertainment"
//        ),
//        "Interests" to listOf(
//            "Open Source", "Startups", "Research", "Hackathons", "Freelancing", "Teaching", "Mentoring"
//        ),
//        "Industry Focus" to listOf(
//            "Healthcare", "Finance", "Education", "Environment", "Music", "Sports", "Travel", "Food & Cooking"
//        )
//    )
//
//    // Flatten all interests for easy access
//    private val allInterests = interestsCategories.values.flatten()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_onboarding_interests, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initializeViews(view)
//        setupClickListeners()
//        setupInterestsDisplay()
//        updateFinishButtonState()
//    }
//
//    private fun initializeViews(view: View) {
//        stepIndicator = view.findViewById(R.id.stepIndicator)
//        titleText = view.findViewById(R.id.titleText)
//        subtitleText = view.findViewById(R.id.subtitleText)
//        interestsContainer = view.findViewById(R.id.interestsContainer)
//        finishButton = view.findViewById(R.id.finishButton)
//        skipButton = view.findViewById(R.id.skipButton)
//        progressBar = view.findViewById(R.id.progressBar)
//
//        // Set initial text
//        stepIndicator.text = "Step 2 of 2"
//        titleText.text = "What are you interested in?"
//        subtitleText.text = "Choose at least $minInterests areas that interest you"
//    }
//
//    private fun setupClickListeners() {
//        finishButton.setOnClickListener {
//            if (selectedInterests.size >= minInterests) {
//                saveInterestsAndFinish()
//            } else {
//                Toast.makeText(
//                    context,
//                    "Please select at least $minInterests interests",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//
//        skipButton.setOnClickListener {
//            // Finish onboarding without saving interests
//            completeOnboarding()
//        }
//    }
//
//    private fun setupInterestsDisplay() {
//        interestsContainer.removeAllViews()
//
//        // Add category headers and interests
//        interestsCategories.forEach { (category, interests) ->
//            // Add category header
//            addCategoryHeader(category)
//
//            // Add interests in this category
//            interests.forEach { interest ->
//                interestsContainer.addView(createInterestChip(interest))
//            }
//        }
//    }
//
//    private fun addCategoryHeader(categoryName: String) {
//        val headerView = LayoutInflater.from(context)
//            .inflate(R.layout.item_category_header, interestsContainer, false)
//        headerView.findViewById<TextView>(R.id.categoryText).text = categoryName
//        interestsContainer.addView(headerView)
//    }
//
//    private fun createInterestChip(interest: String): View {
//        val isSelected = selectedInterests.contains(interest)
//        val layoutRes = if (isSelected) R.layout.chip_interest_selected else R.layout.chip_interest_unselected
//
//        val chipView = LayoutInflater.from(context).inflate(layoutRes, interestsContainer, false)
//        val chipText = chipView.findViewById<TextView>(R.id.chipText)
//        chipText.text = interest
//
//        chipView.setOnClickListener {
//            toggleInterest(interest)
//        }
//
//        return chipView
//    }
//
//    private fun toggleInterest(interest: String) {
//        if (selectedInterests.contains(interest)) {
//            selectedInterests.remove(interest)
//        } else {
//            if (selectedInterests.size < maxInterests) {
//                selectedInterests.add(interest)
//            } else {
//                Toast.makeText(
//                    context,
//                    "Maximum $maxInterests interests allowed",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return
//            }
//        }
//
//        // Refresh the display
//        setupInterestsDisplay()
//        updateFinishButtonState()
//    }
//
//    private fun updateFinishButtonState() {
//        val hasMinimumInterests = selectedInterests.size >= minInterests
//        finishButton.isEnabled = hasMinimumInterests
//        finishButton.alpha = if (hasMinimumInterests) 1.0f else 0.6f
//
//        // Update subtitle with count
//        subtitleText.text = if (selectedInterests.isEmpty()) {
//            "Choose at least $minInterests areas that interest you"
//        } else {
//            "${selectedInterests.size} selected • Choose at least $minInterests interests"
//        }
//    }
//
//    private fun saveInterestsAndFinish() {
//        val currentUser = auth.currentUser ?: return
//
//        showLoading(true)
//
//        lifecycleScope.launch(Dispatchers.Main) {
//            try {
//                // Update user document with selected interests and mark onboarding as complete
//                val updates = mapOf(
//                    "interests" to selectedInterests,
//                    "onboardingCompleted" to true,
//                    "onboardingCompletedAt" to System.currentTimeMillis()
//                )
//
//                db.collection("users").document(currentUser.uid)
//                    .update(updates)
//                    .await()
//
//                showLoading(false)
//                completeOnboarding()
//
//            } catch (e: Exception) {
//                showLoading(false)
//                Toast.makeText(
//                    context,
//                    "Failed to save interests: ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    private fun completeOnboarding() {
//        val currentUser = auth.currentUser ?: return
//
//        // Mark onboarding as completed even if skipped
//        lifecycleScope.launch(Dispatchers.Main) {
//            try {
//                db.collection("users").document(currentUser.uid)
//                    .update("onboardingCompleted", true)
//                    .await()
//
//                Toast.makeText(context, "Welcome to ProjectSwipe!", Toast.LENGTH_SHORT).show()
//
//                // Navigate to main app (home fragment)
//                findNavController().navigate(R.id.action_onboardingInterestsFragment_to_homeFragment)
//
//            } catch (e: Exception) {
//                // Even if this fails, still navigate to main app
//                Toast.makeText(context, "Welcome to ProjectSwipe!", Toast.LENGTH_SHORT).show()
//                findNavController().navigate(R.id.action_onboardingInterestsFragment_to_homeFragment)
//            }
//        }
//    }
//
//    private fun showLoading(isLoading: Boolean) {
//        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        finishButton.isEnabled = !isLoading
//        skipButton.isEnabled = !isLoading
//
//        // Disable all interest chips during loading
//        for (i in 0 until interestsContainer.childCount) {
//            interestsContainer.getChildAt(i).isEnabled = !isLoading
//        }
//    }
//}

package com.first.projectswipe.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.presentation.ui.auth.AuthManager
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingInterestsFragment : Fragment() {

    private lateinit var stepIndicator: TextView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var interestsContainer: FlexboxLayout
    private lateinit var finishButton: Button
    private lateinit var skipButton: TextView
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var authManager: AuthManager

    private val selectedInterests = mutableListOf<String>()
    private val maxInterests = 8
    private val minInterests = 2

    // Comprehensive interests list organized by category
    private val interestsCategories = mapOf(
        "Project Types" to listOf(
            "Web Applications", "Mobile Apps", "Games", "Desktop Software", "APIs", "Chrome Extensions"
        ),
        "Technologies" to listOf(
            "Artificial Intelligence", "Machine Learning", "Blockchain", "IoT", "AR/VR", "Robotics", "Cybersecurity"
        ),
        "Domains" to listOf(
            "E-commerce", "FinTech", "HealthTech", "EdTech", "Social Media", "Productivity Tools", "Entertainment"
        ),
        "Interests" to listOf(
            "Open Source", "Startups", "Research", "Hackathons", "Freelancing", "Teaching", "Mentoring"
        ),
        "Industry Focus" to listOf(
            "Healthcare", "Finance", "Education", "Environment", "Music", "Sports", "Travel", "Food & Cooking"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_interests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
        setupInterestsDisplay()
        updateFinishButtonState()
    }

    private fun initializeViews(view: View) {
        stepIndicator = view.findViewById(R.id.stepIndicator)
        titleText = view.findViewById(R.id.titleText)
        subtitleText = view.findViewById(R.id.subtitleText)
        interestsContainer = view.findViewById(R.id.interestsContainer)
        finishButton = view.findViewById(R.id.finishButton)
        skipButton = view.findViewById(R.id.skipButton)
        progressBar = view.findViewById(R.id.progressBar)

        // Set initial text
        stepIndicator.text = "Step 2 of 2"
        titleText.text = "What are you interested in?"
        subtitleText.text = "Choose at least $minInterests areas that interest you"
    }

    private fun setupClickListeners() {
        finishButton.setOnClickListener {
            if (selectedInterests.size >= minInterests) {
                saveInterestsAndFinish()
            } else {
                Toast.makeText(
                    context,
                    "Please select at least $minInterests interests",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        skipButton.setOnClickListener {
            // Finish onboarding without saving interests
            completeOnboarding()
        }
    }

    private fun setupInterestsDisplay() {
        interestsContainer.removeAllViews()

        // Add category headers and interests
        interestsCategories.forEach { (category, interests) ->
            // Add category header
            addCategoryHeader(category)

            // Add interests in this category
            interests.forEach { interest ->
                interestsContainer.addView(createInterestChip(interest))
            }
        }
    }

    private fun addCategoryHeader(categoryName: String) {
        val headerView = LayoutInflater.from(context)
            .inflate(R.layout.item_category_header, interestsContainer, false)
        headerView.findViewById<TextView>(R.id.categoryText).text = categoryName
        interestsContainer.addView(headerView)
    }

    private fun createInterestChip(interest: String): View {
        val isSelected = selectedInterests.contains(interest)
        val layoutRes = if (isSelected) R.layout.chip_interest_selected else R.layout.chip_interest_unselected

        val chipView = LayoutInflater.from(context).inflate(layoutRes, interestsContainer, false)
        val chipText = chipView.findViewById<TextView>(R.id.chipText)
        chipText.text = interest

        chipView.setOnClickListener {
            toggleInterest(interest)
        }

        return chipView
    }

    private fun toggleInterest(interest: String) {
        if (selectedInterests.contains(interest)) {
            selectedInterests.remove(interest)
        } else {
            if (selectedInterests.size < maxInterests) {
                selectedInterests.add(interest)
            } else {
                Toast.makeText(
                    context,
                    "Maximum $maxInterests interests allowed",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        // Refresh the display
        setupInterestsDisplay()
        updateFinishButtonState()
    }

    private fun updateFinishButtonState() {
        val hasMinimumInterests = selectedInterests.size >= minInterests
        finishButton.isEnabled = hasMinimumInterests
        finishButton.alpha = if (hasMinimumInterests) 1.0f else 0.6f

        // Update subtitle with count
        subtitleText.text = if (selectedInterests.isEmpty()) {
            "Choose at least $minInterests areas that interest you"
        } else {
            "${selectedInterests.size} selected • Choose at least $minInterests interests"
        }
    }

    private fun saveInterestsAndFinish() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                // Update user interests and mark onboarding as complete via your backend API
                val success = authManager.updateUserInterestsAndCompleteOnboarding(selectedInterests)

                showLoading(false)

                if (success) {
                    completeOnboarding()
                } else {
                    Toast.makeText(
                        context,
                        "Failed to save interests. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    context,
                    "Failed to save interests: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun completeOnboarding() {
        lifecycleScope.launch {
            try {
                // Mark onboarding as completed even if skipped
                authManager.completeOnboarding()

                Toast.makeText(context, "Welcome to ProjectSwipe!", Toast.LENGTH_SHORT).show()

                // Navigate to main app (home fragment)
                findNavController().navigate(R.id.action_onboardingInterestsFragment_to_homeFragment)

            } catch (e: Exception) {
                // Even if this fails, still navigate to main app
                Toast.makeText(context, "Welcome to ProjectSwipe!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_onboardingInterestsFragment_to_homeFragment)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        finishButton.isEnabled = !isLoading
        skipButton.isEnabled = !isLoading

        // Disable all interest chips during loading
        for (i in 0 until interestsContainer.childCount) {
            interestsContainer.getChildAt(i).isEnabled = !isLoading
        }
    }
}