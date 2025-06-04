package com.first.projectswipe.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return view
    }

    private fun loadIdeas() {
        db.collection("project_ideas")
            .get()
            .addOnSuccessListener { result ->
                projectIdeas.clear()
                projectIdeas.addAll(result.map { it.toObject(ProjectIdea::class.java) })

                cardStackManager = CardStackManager(
                    context = requireContext(),
                    container = cardContainer,
                    allIdeas = projectIdeas,
                    onCardSwiped = { idea, direction ->
                        Log.d("Swipe", if (direction > 0) "Liked: ${idea.title}" else "Disliked: ${idea.title}")
                        // No need to call restack() here—handled internally!
                    }
                )

                cardStackManager.showInitialCards()
            }
            .addOnFailureListener {
                Log.e("HomeFragment", "Failed to load project ideas", it)
            }
    }
}
