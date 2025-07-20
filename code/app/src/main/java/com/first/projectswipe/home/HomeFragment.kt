package com.first.projectswipe.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
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
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar as androidx.appcompat.widget.Toolbar)

        hamburgerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
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
        db.collection("project_ideas")
            .get()
            .addOnSuccessListener { result ->
                projectIdeas.clear()
                projectIdeas.addAll(result.map { it.toObject(ProjectIdea::class.java) })

                val prefs = requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
                val savedIndex = prefs.getInt("swipe_index", 0)

                cardStackManager = CardStackManager(
                    context = requireContext(),
                    container = cardContainer,
                    allIdeas = projectIdeas,
                    startingIndex = savedIndex,
                    onCardSwiped = { idea, direction ->
                        Log.d("Swipe", if (direction > 0) "Liked: ${idea.title}" else "Disliked: ${idea.title}")
                    }
                )

                cardStackManager.showInitialCards()
            }
            .addOnFailureListener {
                Log.e("HomeFragment", "Failed to load project ideas", it)
            }
    }
}
