package com.first.projectswipe.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.adapters.ProjectCardAdapter
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore

class ProjectIdeasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectCardAdapter
    private val db = FirebaseFirestore.getInstance()
    private val projectIdeas = mutableListOf<ProjectIdea>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_ideas, container, false)
        val cardContainer: FrameLayout = view.findViewById(R.id.cardStackContainer)

        db.collection("project_ideas")
            .get()
            .addOnSuccessListener { result ->
                val ideas = result.map { it.toObject(ProjectIdea::class.java) }
                cardContainer.removeAllViews()

                // Show only top 3 cards for now
                val topCards = ideas.take(3)

                // Add bottom card first, so it's drawn below
                topCards.reversed().forEachIndexed { index, idea ->
                    val cardView = layoutInflater.inflate(R.layout.item_project_card, cardContainer, false)

                    // Offset for stacked depth effect
                    val offset = 32 * index
                    cardView.translationY = offset.toFloat()

                    // Optional: scale smaller cards slightly
                    cardView.scaleX = 1f - (0.05f * index)
                    cardView.scaleY = 1f - (0.05f * index)

                    // Fill content
                    cardView.findViewById<TextView>(R.id.projectTitleTextView).text = idea.title
                    cardView.findViewById<TextView>(R.id.projectDescriptionTextView).text = idea.description

                    val chipGroup = cardView.findViewById<ChipGroup>(R.id.projectTagsChipGroup)
                    chipGroup.removeAllViews()
                    idea.tags?.forEach { tag ->
                        val chip = Chip(requireContext()).apply {
                            text = tag
                            isCheckable = false
                            isClickable = false
                        }
                        chipGroup.addView(chip)
                    }

                    // Optional: set click listener or like/dislike
                    // cardView.findViewById<ImageButton>(R.id.likeButton).setOnClickListener { ... }

                    cardContainer.addView(cardView)
                }
            }
            .addOnFailureListener {
                Log.e("ProjectIdeasFragment", "Failed to load ideas", it)
            }

        return view
    }


    private fun loadIdeas() {
        db.collection("project_ideas")
            .get()
            .addOnSuccessListener { result ->
                projectIdeas.clear()
                for (doc in result) {
                    projectIdeas.add(doc.toObject(ProjectIdea::class.java))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("ProjectIdeasFragment", "Error loading project ideas", it)
            }
    }
}
