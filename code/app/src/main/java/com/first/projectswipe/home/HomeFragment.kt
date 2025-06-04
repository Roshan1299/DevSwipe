package com.first.projectswipe.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.first.projectswipe.utils.CardStackManager
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var cardContainer: FrameLayout
    private lateinit var cardStackManager: CardStackManager

    private val db = FirebaseFirestore.getInstance()
    private val projectIdeas = mutableListOf<ProjectIdea>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))
        cardContainer = view.findViewById(R.id.cardStackContainer)

        loadIdeas()
        val resetButton = view.findViewById<Button>(R.id.resetButton)
        resetButton.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
            prefs.edit().putInt("swipe_index", 0).apply()
            cardStackManager.resetToStart()
        }

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
