package com.first.projectswipe.home

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
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.first.projectswipe.utils.CardStackManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var cardContainer: FrameLayout
    private lateinit var cardStackManager: CardStackManager

    private val db = FirebaseFirestore.getInstance()
    private val projectIdeas = mutableListOf<ProjectIdea>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        drawerLayout = view.findViewById(R.id.homeDrawerLayout)
        navigationView = view.findViewById(R.id.navigationView)
        cardContainer = view.findViewById(R.id.cardStackContainer)
        val toolbar = view.findViewById<View>(R.id.toolbar)
        val hamburgerButton = toolbar.findViewById<ImageButton>(R.id.hamburgerButton)
        val filterButton = toolbar.findViewById<ImageButton>(R.id.filterButton)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar as androidx.appcompat.widget.Toolbar)

        hamburgerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        filterButton.setOnClickListener {
            FilterBottomSheet().show(parentFragmentManager, FilterBottomSheet.TAG)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> Log.d("Drawer", "Profile selected")
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    findNavController().navigate(R.id.action_global_loginFragment)
                }
                else -> Log.d("Drawer", "Clicked: ${menuItem.title}")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        loadIdeas()
        return view
    }

    private fun loadIdeas() {
        Log.d("HomeFragment", "Starting to load project ideas...")

        db.collection("project_ideas")
            .orderBy("title", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                Log.d("HomeFragment", "Firestore query successful. Documents found: ${result.size()}")

                if (result.isEmpty) {
                    Log.w("HomeFragment", "No project ideas found in database")
                    Toast.makeText(requireContext(), "No project ideas found. Try creating some!", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                projectIdeas.clear()

                for (document in result) {
                    try {
                        val projectIdea = document.toObject(ProjectIdea::class.java)

                        if (projectIdea.title.isNotEmpty() && projectIdea.previewDescription.isNotEmpty()) {
                            val finalProjectIdea = if (projectIdea.id.isEmpty()) {
                                projectIdea.copy(id = document.id)
                            } else {
                                projectIdea
                            }

                            projectIdeas.add(finalProjectIdea)
                            Log.d("HomeFragment", "Added project: ${finalProjectIdea.title} with ID: ${finalProjectIdea.id}")
                        } else {
                            Log.w("HomeFragment", "Skipping document ${document.id} - missing required fields")
                        }
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Error converting document ${document.id} to ProjectIdea", e)
                    }
                }

                Log.d("HomeFragment", "Successfully loaded ${projectIdeas.size} project ideas")

                if (projectIdeas.isNotEmpty()) {
                    val prefs = requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
                    val savedIndex = prefs.getInt("swipe_index", 0)
                    val startingIndex = if (savedIndex < projectIdeas.size) savedIndex else 0

                    Log.d("HomeFragment", "Restoring swipe position: $startingIndex (saved: $savedIndex)")

                    cardStackManager = CardStackManager(
                        context = requireContext(),
                        container = cardContainer,
                        allIdeas = projectIdeas,
                        startingIndex = startingIndex,
                        onCardSwiped = { idea, direction ->
                            Log.d("Swipe", if (direction > 0) "Liked: ${idea.title}" else "Disliked: ${idea.title}")
                            saveSwipePosition()
                        }
                    )

                    cardStackManager.showInitialCards()
                    Log.d("HomeFragment", "CardStackManager initialized and cards displayed")
                } else {
                    Log.w("HomeFragment", "No valid project ideas after filtering")
                    Toast.makeText(requireContext(), "No valid project ideas found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Failed to load project ideas", exception)
                Toast.makeText(requireContext(), "Failed to load projects: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveSwipePosition() {
        requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("swipe_index", cardStackManager.currentTopIndex)
            .apply()
    }

    fun applyFilters(filters: Map<String, Any?>) {
        Log.d("FilterDebug", "Applying filters: $filters")

        // If no filters are applied, show all projects
        if (filters.isEmpty()) {
            Log.d("FilterDebug", "No filters applied - showing all projects")
            cardStackManager.updateIdeas(projectIdeas)
            return
        }

        Log.d("FilterDebug", "Original projects count: ${projectIdeas.size}")

        val filteredList = projectIdeas.filter { idea ->
            val difficultyMatch = filters["difficulty"]?.let {
                Log.d("FilterDebug", "Checking difficulty: ${idea.difficulty} vs filter: $it")
                it == idea.difficulty
            } ?: true

            val tagsMatch = (filters["tags"] as? List<String>)?.let { selectedTags ->
                Log.d("FilterDebug", "Checking tags: ${idea.tags} vs filter: $selectedTags")
                selectedTags.isEmpty() || idea.tags.any { it in selectedTags }
            } ?: true

            val matches = difficultyMatch && tagsMatch
            if (matches) Log.d("FilterDebug", "Project ${idea.title} matches filters")
            matches
        }

        Log.d("FilterDebug", "Filtered projects count: ${filteredList.size}")

        if (filteredList.isEmpty()) {
            Log.d("FilterDebug", "No projects match filters - showing empty state")
            cardContainer.removeAllViews()
            val emptyView = LayoutInflater.from(context)
                .inflate(R.layout.empty_state_view, cardContainer, false)
            cardContainer.addView(emptyView)
        } else {
            Log.d("FilterDebug", "Updating card stack with filtered projects")
            cardStackManager.updateIdeas(filteredList)
        }
    }

    override fun onResume() {
        super.onResume()
        loadIdeas()
    }
}