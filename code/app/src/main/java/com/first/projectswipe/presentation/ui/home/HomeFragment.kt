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
import com.first.projectswipe.data.models.ProjectIdea
import com.first.projectswipe.network.ApiService
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    // We'll initialize these manually instead of using Dagger Hilt for now
    private lateinit var authManager: AuthManager
    private lateinit var apiService: ApiService

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var cardContainer: FrameLayout

    // Master list of all fetched ideas (unfiltered)
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

        // Initialize dependencies manually
        authManager = AuthManager.getInstance(requireContext())
        // apiService = ApiService.getInstance() // We'll implement this later

        drawerLayout = view.findViewById(R.id.homeDrawerLayout)
        navigationView = view.findViewById(R.id.navigationView)
        cardContainer = view.findViewById(R.id.cardStackContainer)

        setupToolbar(view)
        setupNavigationDrawer()
        setupFilterButton(view)

        loadIdeas()

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
            // TODO: Implement filter bottom sheet when ready
            // For now, just show a placeholder message
            Toast.makeText(context, "Filter feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    /** Loads all project ideas from your REST API and shows the card stack. */
    private fun loadIdeas() {
        lifecycleScope.launch {
            try {
                // TODO: Replace with your actual API endpoint for project ideas
                // For now, we'll create some sample data to prevent crashes

                allIdeas = getSampleProjectIdeas()
                Log.d("HomeFragment", "Loaded ${allIdeas.size} projects")

                if (allIdeas.isNotEmpty()) {
                    // On first load, reset filters selections so all are shown
                    lastSelectedDifficulty = null
                    lastSelectedTags = emptySet()
                    showCards(allIdeas)
                } else {
                    showEmptyState()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading projects: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("HomeFragment", "Error loading projects", e)
                showEmptyState()
            }
        }
    }

    /** Creates sample project ideas - replace this with API call later */
    private fun getSampleProjectIdeas(): List<ProjectIdea> {
        return listOf(
            ProjectIdea(
                id = "1",
                title = "Task Management App",
                previewDescription = "Create a simple task management app with auth and real-time updates.",
                fullDescription = "Build a comprehensive task management application that includes user authentication, real-time task updates, project organization, team collaboration features, and deadline tracking. This project will help you learn modern Android development patterns and backend integration.",
                createdBy = "user123",
                createdByName = "John Developer",
                difficulty = "Beginner",
                tags = listOf("Android", "Kotlin", "REST API"),
                githubLink = "https://github.com/example/task-manager",
                timeline = "2-3 weeks"
            ),
            ProjectIdea(
                id = "2",
                title = "Weather Dashboard",
                previewDescription = "Build a weather dashboard with forecasts using external APIs.",
                fullDescription = "Develop a comprehensive weather dashboard that displays current weather conditions, 7-day forecasts, weather maps, and alerts. Integrate with multiple weather APIs for accurate data and create beautiful data visualizations with charts and graphs.",
                createdBy = "user456",
                createdByName = "Sarah WebDev",
                difficulty = "Intermediate",
                tags = listOf("Web", "JavaScript", "API"),
                githubLink = "https://github.com/example/weather-dashboard",
                timeline = "1-2 weeks"
            ),
            ProjectIdea(
                id = "3",
                title = "E-commerce Platform",
                previewDescription = "Develop a full e-commerce platform with payments and admin panel.",
                fullDescription = "Create a complete e-commerce solution with product catalog, shopping cart, payment processing, order management, user accounts, admin dashboard, inventory tracking, and analytics. This full-stack project covers everything from frontend to backend to database design.",
                createdBy = "user789",
                createdByName = "Mike FullStack",
                difficulty = "Advanced",
                tags = listOf("Full-Stack", "Database", "Payments"),
                githubLink = "https://github.com/example/ecommerce-platform",
                timeline = "2-3 months"
            ),
            ProjectIdea(
                id = "4",
                title = "Social Media Clone",
                previewDescription = "Build a social media app with posts, likes, and comments.",
                fullDescription = "Create a social media platform similar to Instagram or Twitter with user profiles, photo/text posts, likes, comments, following system, real-time notifications, and discover feed. Learn about social app architecture and real-time features.",
                createdBy = "user101",
                createdByName = "Alex SocialDev",
                difficulty = "Intermediate",
                tags = listOf("Mobile", "Social", "Real-time"),
                githubLink = "https://github.com/example/social-clone",
                timeline = "4-6 weeks"
            ),
            ProjectIdea(
                id = "5",
                title = "AI Chatbot Assistant",
                previewDescription = "Create an AI-powered chatbot with natural language processing.",
                fullDescription = "Build an intelligent chatbot assistant that can understand natural language, provide helpful responses, integrate with various APIs for information retrieval, and learn from user interactions. Implement machine learning models and NLP techniques.",
                createdBy = "user202",
                createdByName = "Emma AIExpert",
                difficulty = "Advanced",
                tags = listOf("AI", "NLP", "Machine Learning"),
                githubLink = "https://github.com/example/ai-chatbot",
                timeline = "6-8 weeks"
            )
        )
    }

    /** Rebuilds the card stack UI with [ideas]. */
    private fun showCards(ideas: List<ProjectIdea>) {
        displayedIdeas = ideas
        cardContainer.removeAllViews()

        // TODO: Implement CardStackManager when ready
        // For now, show a simple message
        val textView = android.widget.TextView(context).apply {
            text = "Found ${ideas.size} project ideas!\n\nCard stack feature will be implemented next."
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(32, 32, 32, 32)
            textSize = 16f
        }
        cardContainer.addView(textView)

        saveSwipePosition(0)
    }

    /** Applies difficulty/tags filters and shows filtered projects */
    fun applyFilters(filters: Map<String, Any?>) {
        // If no filters, show all projects
        if (filters.isEmpty()) {
            showCards(allIdeas)
            return
        }

        val filtered = allIdeas.filter { idea ->
            val difficultyMatch = filters["difficulty"]?.let { diff ->
                idea.difficulty.equals(diff.toString(), ignoreCase = true)
            } ?: true

            val tagsMatch = filters["tags"]?.let { tagList ->
                if (tagList is List<*> && tagList.isNotEmpty()) {
                    val selectedTags = tagList.filterIsInstance<String>()
                    selectedTags.isEmpty() || idea.tags.any { tag -> selectedTags.contains(tag) }
                } else true
            } ?: true

            difficultyMatch && tagsMatch
        }

        if (filtered.isNotEmpty()) {
            showCards(filtered)
        } else {
            showEmptyState()
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