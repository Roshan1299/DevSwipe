package com.first.projectswipe.presentation.ui.collaboration

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.presentation.ui.auth.AuthManager
import com.first.projectswipe.network.toCollabPost
import com.first.projectswipe.data.models.CollabPost
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.utils.CollabCardStackManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SeekingCollaboratorsFragment : Fragment() {

    // Inject dependencies using Hilt
    @Inject
    lateinit var authManager: AuthManager
    
    @Inject
    lateinit var apiService: ApiService

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var cardContainer: FrameLayout
    private var cardStackManager: CollabCardStackManager? = null

    private var allCollabPosts: List<CollabPost> = emptyList()

    // Store last applied filter selections to keep them persistent in the filter sheet
    private var lastSelectedTimeCommitment: String? = null
    private var lastSelectedSkills: Set<String> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_collaborate, container, false)

        // Dependencies are injected via Hilt

        drawerLayout = view.findViewById(R.id.collaborateDrawerLayout)
        navigationView = view.findViewById(R.id.navigationView)
        cardContainer = view.findViewById(R.id.cardStackContainer)

        setupToolbar(view)
        setupNavigationDrawer()
        setupFilterButton(view)
        authManager.isLoggedIn.observe(viewLifecycleOwner) {
            if (it) {
                loadCollabPosts()
            } else {
                // Handle not logged in case if needed
            }
        }

        return view
    }

    private fun setupToolbar(view: View) {
        val toolbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        val hamburgerButton = toolbar.findViewById<ImageButton>(R.id.hamburgerButton)
        hamburgerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    Log.d("Drawer", "Profile selected")
                    // TODO: Navigate to profile when implemented
                }
                R.id.nav_logout -> {
                    logout()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupFilterButton(view: View) {
        val filterButton = view.findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener {
            // For now, we will just reload the data
            // In the future, we can implement proper filter functionality similar to the home fragment
            loadCollabPosts()
        }
    }

    /** Loads all collaboration posts from your REST API and shows the card stack. */
    private fun loadCollabPosts() {
        lifecycleScope.launch {
            try {
                val response = apiService.getCollaborations()
                if (response.isSuccessful) {
                    allCollabPosts = response.body()?.map { it.toCollabPost() } ?: emptyList()
                    Log.d("CollabFragment", "Loaded ${allCollabPosts.size} collaboration posts")

                    if (allCollabPosts.isNotEmpty()) {
                        val startingIndex = requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
                            .getInt("collab_swipe_index", 0)
                        showCards(allCollabPosts, startingIndex)
                    } else {
                        showEmptyState()
                    }
                } else {
                    Toast.makeText(context, "Error loading collaboration posts: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("CollabFragment", "Error loading collaboration posts: ${response.code()} ${response.message()}")
                    showEmptyState()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading collaboration posts: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("CollabFragment", "Error loading collaboration posts", e)
                showEmptyState()
            }
        }
    }

    /** Rebuilds the card stack UI with [collabPosts]. */
    private fun showCards(collabPosts: List<CollabPost>, startingIndex: Int = 0) {
        cardContainer.removeAllViews()
        cardStackManager = CollabCardStackManager(
            context = requireContext(),
            container = cardContainer,
            allCollabPosts = collabPosts,
            apiService = apiService,
            startingIndex = startingIndex,
            onCardSwiped = { _, _ ->
                saveSwipePosition(cardStackManager?.currentTopIndex ?: 0)
            }
        ).also { it.showInitialCards() }
        saveSwipePosition(startingIndex)
    }

    /** Shows the empty state view if there are no collaboration posts */
    private fun showEmptyState() {
        cardContainer.removeAllViews()

        val emptyTextView = android.widget.TextView(context).apply {
            text = "No collaboration posts found.\n\nTry checking back later!"
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(32, 32, 32, 32)
            textSize = 16f
        }
        cardContainer.addView(emptyTextView)
    }

    /** Save the card stack position for restoring on return */
    private fun saveSwipePosition(index: Int) {
        requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("collab_swipe_index", index)
            .apply()
    }

    private fun logout() {
        lifecycleScope.launch {
            try {
                authManager.logout()
                // Navigate back to login
                findNavController().navigate(R.id.action_global_loginFragment)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Logout failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
