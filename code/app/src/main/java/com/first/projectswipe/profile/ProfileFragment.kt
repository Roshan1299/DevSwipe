package com.first.projectswipe.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.adapters.ProfilePostAdapter
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var universityTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var skillsChipGroup: ChipGroup
    private lateinit var interestsChipGroup: ChipGroup
    private lateinit var postsRecyclerView: RecyclerView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val postList = mutableListOf<ProjectIdea>()
    private lateinit var adapter: ProfilePostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Bind views
        profileImageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.profileName)
        universityTextView = view.findViewById(R.id.profileUniversity)
        bioTextView = view.findViewById(R.id.profileBio)
        skillsChipGroup = view.findViewById(R.id.skillsChipGroup)
        interestsChipGroup = view.findViewById(R.id.interestsChipGroup)
        postsRecyclerView = view.findViewById(R.id.userPostsRecyclerView)

        // Setup RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProfilePostAdapter(postList)
        postsRecyclerView.adapter = adapter

        loadUserProfile()
        loadUserPosts()

        return view
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                nameTextView.text = document.getString("name") ?: "Anonymous"
                universityTextView.text = document.getString("university") ?: ""
                bioTextView.text = document.getString("bio") ?: ""

                val skills = document.get("skills") as? List<String> ?: emptyList()
                val interests = document.get("interests") as? List<String> ?: emptyList()

                addChips(skillsChipGroup, skills)
                addChips(interestsChipGroup, interests)

                val profilePicUrl = document.getString("profileImageUrl")
                if (!profilePicUrl.isNullOrEmpty()) {
                    Picasso.get().load(profilePicUrl)
                        .into(profileImageView)
                }
            }
        }
    }

    private fun loadUserPosts() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("project_ideas")
            .whereEqualTo("createdBy", userId)
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                for (doc in result) {
                    val idea = doc.toObject(ProjectIdea::class.java)
                    postList.add(idea)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun addChips(chipGroup: ChipGroup, items: List<String>) {
        chipGroup.removeAllViews()
        for (item in items) {
            val chip = Chip(requireContext())
            chip.text = item
            chip.isClickable = false
            chip.isCheckable = false
//            chip.setChipBackgroundColorResource(R.color.chip_background)
            chip.setTextColor(Color.BLACK)
            chipGroup.addView(chip)
        }
    }
}
