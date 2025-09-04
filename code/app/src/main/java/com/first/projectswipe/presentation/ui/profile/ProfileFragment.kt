package com.first.projectswipe.presentation.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.presentation.adapters.ProfilePostAdapter
import com.first.projectswipe.data.models.ProjectIdea
import com.first.projectswipe.data.repository.ProfileRepository
import com.first.projectswipe.network.dto.UserProfileResponse
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var universityTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var skillsChipGroup: ChipGroup
    private lateinit var interestsChipGroup: ChipGroup
    private lateinit var postsRecyclerView: RecyclerView

    // Replace Firebase with your backend
    @Inject
    lateinit var profileRepository: ProfileRepository

    private val postList = mutableListOf<ProjectIdea>()
    private lateinit var adapter: ProfilePostAdapter
    private var currentProfile: UserProfileResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        // Bind views
        profileImageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.profileName)
        universityTextView = view.findViewById(R.id.profileUniversity)
        bioTextView = view.findViewById(R.id.profileBio)
        skillsChipGroup = view.findViewById(R.id.skillsChipGroup)
        interestsChipGroup = view.findViewById(R.id.interestsChipGroup)
        postsRecyclerView = view.findViewById(R.id.userPostsRecyclerView)

        val editBtn = view.findViewById<ImageButton>(R.id.editProfileButton)
        editBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Setup RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProfilePostAdapter(postList)
        postsRecyclerView.adapter = adapter

        loadUserProfile()
        loadUserPosts()

        return view
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                profileRepository.getCurrentProfile()
                    .onSuccess { profile ->
                        currentProfile = profile
                        displayProfile(profile)
                    }
                    .onFailure { error ->
                        showError("Failed to load profile: ${error.message}")
                        // If profile doesn't exist, navigate to create profile
                        if (error.message?.contains("Profile not found") == true) {
                            navigateToCreateProfile()
                        }
                    }
            } catch (e: Exception) {
                showError("Error loading profile: ${e.message}")
            }
        }
    }

    private fun displayProfile(profile: UserProfileResponse) {
        nameTextView.text = profile.name
        universityTextView.text = profile.university ?: ""
        bioTextView.text = profile.bio ?: ""

        // Add chips for skills and interests
        profile.skills?.let { addChips(skillsChipGroup, it) }
        profile.interests?.let { addChips(interestsChipGroup, it) }

        // Load profile image
        profile.profileImageUrl?.let { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder) // Add this drawable
                    .error(R.drawable.ic_profile_placeholder)
                    .into(profileImageView)
            }
        }
    }

    private fun loadUserPosts() {
        // TODO: Implement when you have project API endpoints
        // For now, you can keep this empty or load from your project API

        lifecycleScope.launch {
            try {
                // When you implement project endpoints:
                // val projects = projectRepository.getCurrentUserProjects()
                // postList.clear()
                // postList.addAll(projects)
                // adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                showError("Failed to load posts: ${e.message}")
            }
        }
    }

    private fun addChips(chipGroup: ChipGroup, items: List<String>) {
        chipGroup.removeAllViews()
        for (item in items) {
            val chip = Chip(requireContext())
            chip.text = item
            chip.isClickable = false
            chip.isCheckable = false
            chip.setTextColor(Color.BLACK)
            chipGroup.addView(chip)
        }
    }

    private fun navigateToCreateProfile() {
        // Navigate to profile creation/edit screen
        try {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        } catch (e: Exception) {
            showError("Please create your profile first")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}