//// File: com/first/projectswipe/home/HomeFragment.kt
//
//package com.first.projectswipe.presentation.ui.home
//
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.ImageButton
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.first.projectswipe.R
//import com.first.projectswipe.data.models.ProjectIdea
//import com.first.projectswipe.utils.CardStackManager
//import com.google.android.material.navigation.NavigationView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//class HomeFragment : Fragment() {
//
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var navigationView: NavigationView
//    private lateinit var cardContainer: FrameLayout
//
//    // Card stack manager, recreated when data changes or filters apply
//    private var cardStackManager: CardStackManager? = null
//
//    private val db = FirebaseFirestore.getInstance()
//
//    // Master list of all fetched ideas (unfiltered)
//    private var allIdeas: List<ProjectIdea> = emptyList()
//
//    // Currently displayed ideas after filtering (or allIdeas)
//    private var displayedIdeas: List<ProjectIdea> = emptyList()
//
//    // Store last applied filter selections to keep them persistent in the filter sheet
//    private var lastSelectedDifficulty: String? = null
//    private var lastSelectedTags: Set<String> = emptySet()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        drawerLayout = view.findViewById(R.id.homeDrawerLayout)
//        navigationView = view.findViewById(R.id.navigationView)
//        cardContainer = view.findViewById(R.id.cardStackContainer)
//
//        setupToolbar(view)
//        setupNavigationDrawer()
//        setupFilterButton(view)
//
//        loadIdeas()
//
//        return view
//    }
//
//    private fun setupToolbar(view: View) {
//        val toolbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
//        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
//        val hamburgerButton = toolbar.findViewById<ImageButton>(R.id.hamburgerButton)
//        hamburgerButton.setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }
//    }
//
//    private fun setupNavigationDrawer() {
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_profile -> Log.d("Drawer", "Profile selected")
//                R.id.nav_logout -> {
//                    FirebaseAuth.getInstance().signOut()
//                    findNavController().navigate(R.id.action_global_loginFragment)
//                }
//            }
//            drawerLayout.closeDrawer(GravityCompat.START)
//            true
//        }
//    }
//
//    private fun setupFilterButton(view: View) {
//        val filterButton = view.findViewById<ImageButton>(R.id.filterButton)
//        filterButton.setOnClickListener {
//            // Open filter bottom sheet passing current filter selections to preserve UI state
//            val sheet = FilterBottomSheet.newInstance(lastSelectedDifficulty, lastSelectedTags)
//            sheet.setFilterListener(object : FilterBottomSheet.FilterListener {
//                override fun onFiltersSelected(filterMap: Map<String, Any?>) {
//                    // Save current filter selections for persistence
//                    lastSelectedDifficulty = filterMap["difficulty"] as? String
//                    lastSelectedTags =
//                        (filterMap["tags"] as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()
//
//                    // Apply filters to update cards
//                    applyFilters(filterMap)
//                }
//            })
//            sheet.show(parentFragmentManager, FilterBottomSheet.TAG)
//        }
//    }
//
//    /** Loads all project ideas from Firestore and shows the card stack. */
//    private fun loadIdeas() {
//        CoroutineScope(Dispatchers.Main).launch {
//            try {
//                val result = db.collection("project_ideas")
//                    .orderBy("title", Query.Direction.ASCENDING)
//                    .get()
//                    .await()
//
//                allIdeas = result.documents.mapNotNull { document ->
//                    document.toObject(ProjectIdea::class.java)?.copy(id = document.id)
//                }
//                Log.d("HomeFragment", "Fetched ${allIdeas.size} projects")
//
//                if (allIdeas.isNotEmpty()) {
//                    // On first load, reset filters selections so all are shown
//                    lastSelectedDifficulty = null
//                    lastSelectedTags = emptySet()
//                    showCards(allIdeas)
//                } else {
//                    showEmptyState()
//                }
//            } catch (e: Exception) {
//                Toast.makeText(context, "Error loading projects: ${e.message}", Toast.LENGTH_SHORT).show()
//                Log.e("HomeFragment", "Error loading projects", e)
//                showEmptyState()
//            }
//        }
//    }
//
//    /** Rebuilds the card stack UI with [ideas]. */
//    private fun showCards(ideas: List<ProjectIdea>) {
//        displayedIdeas = ideas
//        cardContainer.removeAllViews()
//        cardStackManager = CardStackManager(
//            context = requireContext(),
//            container = cardContainer,
//            allIdeas = displayedIdeas,
//            startingIndex = 0,
//            onCardSwiped = { _, _ ->
//                saveSwipePosition(cardStackManager?.currentTopIndex ?: 0)
//            }
//        ).also { it.showInitialCards() }
//        saveSwipePosition(0)
//    }
//
//    /** Applies difficulty/tags filters and shows filtered projects */
//    fun applyFilters(filters: Map<String, Any?>) {
//        // If no filters, show all projects
//        if (filters.isEmpty()) {
//            showCards(allIdeas)
//            return
//        }
//
//        val filtered = allIdeas.filter { idea ->
//            val difficultyMatch = filters["difficulty"]?.let { diff ->
//                idea.difficulty.equals(diff.toString(), ignoreCase = true)
//            } ?: true
//
//            val tagsMatch = filters["tags"]?.let { tagList ->
//                if (tagList is List<*> && tagList.isNotEmpty()) {
//                    val selectedTags = tagList.filterIsInstance<String>()
//                    selectedTags.isEmpty() || idea.tags.any { tag -> selectedTags.contains(tag) }
//                } else true
//            } ?: true
//
//            difficultyMatch && tagsMatch
//        }
//
//        if (filtered.isNotEmpty()) {
//            showCards(filtered)
//        } else {
//            showEmptyState()
//        }
//    }
//
//    /** Shows the empty state view if there are no projects */
//    private fun showEmptyState() {
//        cardContainer.removeAllViews()
//        val emptyView = LayoutInflater.from(context)
//            .inflate(R.layout.empty_state_view, cardContainer, false)
//        cardContainer.addView(emptyView)
//    }
//
//    /** Save the card stack position for restoring on return */
//    private fun saveSwipePosition(index: Int) {
//        requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
//            .edit()
//            .putInt("swipe_index", index)
//            .apply()
//    }
//}

package com.first.projectswipe.presentation.ui.home

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
import com.first.projectswipe.network.toProjectIdea
import com.first.projectswipe.data.models.ProjectIdea
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.utils.CardStackManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    // Inject dependencies using Hilt
    @Inject
    lateinit var authManager: AuthManager
    
    @Inject
    lateinit var apiService: ApiService

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var cardContainer: FrameLayout
    private var cardStackManager: CardStackManager? = null


    private var allIdeas: List<ProjectIdea> = emptyList()

    // Currently displayed ideas after filtering (or allIdeas)
    private var displayedIdeas: List<ProjectIdea> = emptyList()

    // Store last applied filter selections to keep them persistent in the filter sheet
    private var lastSelectedDifficulty: String? = null
    private var lastSelectedTags: Set<String> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Dependencies are injected via Hilt

        drawerLayout = view.findViewById(R.id.homeDrawerLayout)
        navigationView = view.findViewById(R.id.navigationView)
        cardContainer = view.findViewById(R.id.cardStackContainer)

        setupToolbar(view)
        setupNavigationDrawer()
        setupFilterButton(view)
        authManager.isLoggedIn.observe(viewLifecycleOwner) {
            if (it) {
                loadIdeas()
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
            val sheet = FilterBottomSheet.newInstance(lastSelectedDifficulty, lastSelectedTags)
            sheet.setFilterListener(object : FilterBottomSheet.FilterListener {
                override fun onFiltersSelected(filterMap: Map<String, Any?>) {
                    lastSelectedDifficulty = filterMap["difficulty"] as? String
                    lastSelectedTags =
                        (filterMap["tags"] as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()

                    applyFilters(filterMap)
                }
            })
            sheet.show(parentFragmentManager, FilterBottomSheet.TAG)
        }
    }

    /** Loads all project ideas from your REST API and shows the card stack. */
    private fun loadIdeas() {
        lifecycleScope.launch {
            try {
                val response = apiService.getProjects()
                if (response.isSuccessful) {
                    allIdeas = response.body()?.map { it.toProjectIdea() } ?: emptyList()
                    Log.d("HomeFragment", "Loaded ${allIdeas.size} projects")

                    if (allIdeas.isNotEmpty()) {
                        val startingIndex = requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
                            .getInt("swipe_index", 0)
                        // On first load, reset filters selections so all are shown
                        lastSelectedDifficulty = null
                        lastSelectedTags = emptySet()
                        showCards(allIdeas, startingIndex)
                    } else {
                        showEmptyState()
                    }
                } else {
                    Toast.makeText(context, "Error loading projects: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("HomeFragment", "Error loading projects: ${response.code()} ${response.message()}")
                    showEmptyState()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading projects: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Error loading projects", e)
                showEmptyState()
            }
        }
    }



    /** Rebuilds the card stack UI with [ideas]. */
    private fun showCards(ideas: List<ProjectIdea>, startingIndex: Int = 0) {
        displayedIdeas = ideas
        cardContainer.removeAllViews()
        cardStackManager = CardStackManager(
            context = requireContext(),
            container = cardContainer,
            allIdeas = displayedIdeas,
            apiService = apiService,
            fragment = this,
            startingIndex = startingIndex,
            onCardSwiped = { _, _ ->
                saveSwipePosition(cardStackManager?.currentTopIndex ?: 0)
            }
        ).also { it.showInitialCards() }
        saveSwipePosition(startingIndex)
    }

    /** Applies difficulty/tags filters and shows filtered projects */
    fun applyFilters(filters: Map<String, Any?>) {
        lifecycleScope.launch {
            try {
                val difficulty = filters["difficulty"] as? String
                val tags = filters["tags"] as? List<String>

                val response = if (difficulty == null && (tags == null || tags.isEmpty())) {
                    // No filters applied - get all projects
                    apiService.getProjects()
                } else {
                    // Apply filters - Retrofit will handle the List<String> conversion to query parameters
                    apiService.filterProjects(difficulty, tags)
                }

                if (response.isSuccessful) {
                    val filteredIdeas = response.body()?.map { it.toProjectIdea() } ?: emptyList()
                    if (filteredIdeas.isNotEmpty()) {
                        showCards(filteredIdeas)
                    } else {
                        showEmptyState()
                    }
                } else {
                    Toast.makeText(context, "Error filtering projects: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("HomeFragment", "Error filtering projects: ${response.code()} ${response.message()}")
                    showEmptyState()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error filtering projects: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Error filtering projects", e)
                showEmptyState()
            }
        }
    }

    /** Shows the empty state view if there are no projects */
    private fun showEmptyState() {
        cardContainer.removeAllViews()

        val emptyTextView = android.widget.TextView(context).apply {
            text = "No projects found.\n\nTry adjusting your filters or check back later!"
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
            .putInt("swipe_index", index)
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