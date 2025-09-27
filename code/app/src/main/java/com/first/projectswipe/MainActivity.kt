// MainActivity.kt
package com.first.projectswipe

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.first.projectswipe.presentation.ui.auth.AuthManager
import com.first.projectswipe.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Inject AuthManager using Hilt
    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup custom bottom navigation
        setupBottomNavigation()

        // Hide bottom nav on login/register screens, CreatePostFragment, and EditProfileFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment,
                R.id.onboardingSkillsFragment,
                R.id.onboardingInterestsFragment,
                R.id.registerFragment,
                R.id.createProjectIdeaFragment,
//                R.id.ResetPasswordFragment,
                R.id.editProfileFragment -> {
                    binding.customBottomNav.visibility = View.GONE
                    binding.addButtons.visibility = View.GONE
                }
                else -> {
                    binding.customBottomNav.visibility = View.VISIBLE
                    binding.addButtons.visibility = View.VISIBLE
                }
            }
        }

        // Check authentication status and navigate accordingly
        checkAuthenticationStatus(savedInstanceState)
    }

    private fun checkAuthenticationStatus(savedInstanceState: Bundle?) {
        // Only check auth on initial app launch, not on configuration changes
        if (savedInstanceState == null) {
            if (authManager.isUserLoggedIn()) {
                // User is logged in, navigate to home
                navController.navigate(
                    R.id.homeFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()
                )
            }
            // If user is not logged in, stay on the current fragment (login screen)
        }
    }

    private fun setupBottomNavigation() {
        // Set initial selected state
        updateBottomNavSelection(R.id.homeFragment)

        // Ideas button
        binding.ideasButton.setOnClickListener {
            navigateToFragment(R.id.homeFragment)
        }

        // Add button (floating action button)
        binding.addButtons.setOnClickListener {
            handleAddButtonClick()
        }

        // Profile button
        binding.profileButton.setOnClickListener {
            navigateToFragment(R.id.profileFragment)
        }

        // Listen for navigation changes to update selection
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavSelection(destination.id)
        }
    }

    private fun navigateToFragment(fragmentId: Int) {
        if (navController.currentDestination?.id != fragmentId) {
            try {
                navController.navigate(fragmentId)
            } catch (e: IllegalArgumentException) {
                // Handle navigation error gracefully
                e.printStackTrace()
            }
        }
    }

    private fun handleAddButtonClick() {
        // Add button click animation
        animateAddButton()

        // Navigate to create post fragment
        try {
            navController.navigate(R.id.createProjectIdeaFragment)
        } catch (e: IllegalArgumentException) {
            // Handle navigation error gracefully
            e.printStackTrace()
        }
    }

    private fun animateAddButton() {
        binding.addButtons.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(100)
            .withEndAction {
                binding.addButtons.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    private fun updateBottomNavSelection(selectedId: Int) {
        // Reset all buttons to unselected state
        binding.collaborateButton.isSelected = false
        binding.ideasButton.isSelected = false
        binding.chatButton.isSelected = false
        binding.profileButton.isSelected = false

        binding.collaborateText.isSelected = false
        binding.ideasText.isSelected = false
        binding.chatText.isSelected = false
        binding.profileText.isSelected = false

        binding.collaborateIcon.isSelected = false
        binding.ideasIcon.isSelected = false
        binding.chatIcon.isSelected = false
        binding.profileIcon.isSelected = false

        // Set selected button
        when (selectedId) {
            R.id.homeFragment -> {
                binding.ideasButton.isSelected = true
                binding.ideasText.isSelected = true
                binding.ideasIcon.isSelected = true
            }
            R.id.profileFragment -> {
                binding.profileButton.isSelected = true
                binding.profileText.isSelected = true
                binding.profileIcon.isSelected = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}