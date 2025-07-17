package com.first.projectswipe

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.first.projectswipe.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup custom bottom navigation
        setupBottomNavigation()

        // Hide bottom nav on login/register screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> binding.customBottomNav.visibility = View.GONE
                else -> binding.customBottomNav.visibility = View.VISIBLE
            }
        }

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Navigate directly to ideas (home) if already logged in
        if (currentUser != null && savedInstanceState == null) {
            navController.navigate(
                R.id.homeFragment, // Changed to ideasFragment as default
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        }
    }

    private fun setupBottomNavigation() {
        // Set initial selected state
        updateBottomNavSelection(R.id.homeFragment)

        // Collaborate button
//        binding.collaborateButton.setOnClickListener {
//            navigateToFragment(R.id.collaborateFragment)
//        }

        // Ideas button
        binding.ideasButton.setOnClickListener {
            navigateToFragment(R.id.homeFragment)
        }

        // Add button (floating action button)
        binding.addButton.setOnClickListener {
            navigateToFragment(R.id.createPostFragment)
        }

        // Chat button
//        binding.chatButton.setOnClickListener {
//            navigateToFragment(R.id.chatFragment)
//        }

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
            navController.navigate(fragmentId)
        }
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
//            R.id.collaborateFragment -> {
//                binding.collaborateButton.isSelected = true
//                binding.collaborateText.isSelected = true
//                binding.collaborateIcon.isSelected = true
//            }
            R.id.homeFragment -> {
                binding.ideasButton.isSelected = true
                binding.ideasText.isSelected = true
                binding.ideasIcon.isSelected = true
            }
//            R.id.chatFragment -> {
//                binding.chatButton.isSelected = true
//                binding.chatText.isSelected = true
//                binding.chatIcon.isSelected = true
//            }
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