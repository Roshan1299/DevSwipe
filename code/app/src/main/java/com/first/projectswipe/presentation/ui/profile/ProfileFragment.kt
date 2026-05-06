package com.first.projectswipe.presentation.ui.profile

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.presentation.adapters.ProfilePostAdapter
import com.first.projectswipe.data.models.ProjectIdea
import com.first.projectswipe.data.repository.ProfileRepository
import com.first.projectswipe.data.repository.ProjectRepository
import com.first.projectswipe.network.dto.UserProfileResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var aboutMeCard: LinearLayout
    private lateinit var skillsChipGroup: ChipGroup
    private lateinit var interestsChipGroup: ChipGroup
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var messageButton: FloatingActionButton

    // Stats
    private lateinit var projectsCount: TextView
    private lateinit var skillsCount: TextView
    private lateinit var interestsCount: TextView

    // Social
    private lateinit var socialLinksContainer: LinearLayout
    private lateinit var githubButton: LinearLayout
    private lateinit var linkedinButton: LinearLayout

    // Posts
    private lateinit var postsCountBadge: TextView
    private lateinit var postsHeaderText: TextView
    private lateinit var emptyPostsLayout: LinearLayout

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var projectRepository: ProjectRepository

    private val postList = mutableListOf<ProjectIdea>()
    private lateinit var adapter: ProfilePostAdapter
    private var currentProfile: UserProfileResponse? = null
    private var viewedUserId: String? = null

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
        aboutMeCard = view.findViewById(R.id.aboutMeCard)
        skillsChipGroup = view.findViewById(R.id.skillsChipGroup)
        interestsChipGroup = view.findViewById(R.id.interestsChipGroup)
        postsRecyclerView = view.findViewById(R.id.userPostsRecyclerView)
        messageButton = view.findViewById(R.id.messageButton)

        // Stats
        projectsCount = view.findViewById(R.id.projectsCount)
        skillsCount = view.findViewById(R.id.skillsCount)
        interestsCount = view.findViewById(R.id.interestsCount)

        // Social
        socialLinksContainer = view.findViewById(R.id.socialLinksContainer)
        githubButton = view.findViewById(R.id.githubButton)
        linkedinButton = view.findViewById(R.id.linkedinButton)

        // Posts
        postsCountBadge = view.findViewById(R.id.postsCountBadge)
        postsHeaderText = view.findViewById(R.id.postsHeaderText)
        emptyPostsLayout = view.findViewById(R.id.emptyPostsLayout)

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val editBtn = view.findViewById<ImageButton>(R.id.editProfileButton)

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        editBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        messageButton.setOnClickListener {
            viewedUserId?.let { userId ->
                val bundle = Bundle().apply {
                    putString("otherUserId", userId)
                }
                findNavController().navigate(
                    R.id.action_profileFragment_to_individualChatFragment,
                    bundle
                )
            }
        }

        // Setup RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProfilePostAdapter(postList) { project, position ->
            deleteProject(project, position)
        }
        postsRecyclerView.adapter = adapter

        // Check if viewing another user's profile
        arguments?.let { bundle ->
            viewedUserId = bundle.getString("viewedUserId")
        }

        // Hide edit button when viewing someone else's profile
        if (viewedUserId != null) {
            editBtn.visibility = View.GONE
            postsHeaderText.text = "Their Projects"
        }

        loadUserProfile()
        loadUserPosts()

        return view
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val result = if (viewedUserId != null) {
                    profileRepository.getUserProfile(viewedUserId!!)
                } else {
                    profileRepository.getCurrentProfile()
                }

                result
                    .onSuccess { profile ->
                        currentProfile = profile
                        displayProfile(profile)

                        if (viewedUserId != null) {
                            messageButton.show()
                        } else {
                            messageButton.hide()
                        }
                    }
                    .onFailure { error ->
                        showError("Failed to load profile: ${error.message}")
                        if (viewedUserId == null) {
                            if (error.message?.contains("Profile not found") == true) {
                                navigateToCreateProfile()
                            }
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

        // About me card
        if (!profile.bio.isNullOrBlank()) {
            aboutMeCard.visibility = View.VISIBLE
            bioTextView.text = profile.bio
        } else {
            aboutMeCard.visibility = View.GONE
        }

        // Stats
        skillsCount.text = (profile.skills?.size ?: 0).toString()
        interestsCount.text = (profile.interests?.size ?: 0).toString()

        // Skills chips - blue themed
        profile.skills?.let { skills ->
            addStyledChips(skillsChipGroup, skills, "#007AFF", "#E8F2FF")
        }

        // Interests chips - green themed
        profile.interests?.let { interests ->
            addStyledChips(interestsChipGroup, interests, "#34C759", "#E8F8ED")
        }

        // Social links
        val hasGithub = !profile.githubUrl.isNullOrBlank()
        val hasLinkedin = !profile.linkedinUrl.isNullOrBlank()

        if (hasGithub || hasLinkedin) {
            socialLinksContainer.visibility = View.VISIBLE

            if (hasGithub) {
                githubButton.visibility = View.VISIBLE
                githubButton.setOnClickListener {
                    openUrl(profile.githubUrl!!)
                }
            } else {
                githubButton.visibility = View.GONE
            }

            if (hasLinkedin) {
                linkedinButton.visibility = View.VISIBLE
                linkedinButton.setOnClickListener {
                    openUrl(profile.linkedinUrl!!)
                }
            } else {
                linkedinButton.visibility = View.GONE
            }
        } else {
            socialLinksContainer.visibility = View.GONE
        }

        // Load profile image
        profile.profileImageUrl?.let { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(profileImageView)
            }
        }
    }

    private fun addStyledChips(chipGroup: ChipGroup, items: List<String>, textColorHex: String, bgColorHex: String) {
        chipGroup.removeAllViews()
        for (item in items) {
            val chip = Chip(requireContext())
            chip.text = item
            chip.isClickable = false
            chip.isCheckable = false
            chip.setTextColor(Color.parseColor(textColorHex))
            chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(bgColorHex))
            chip.chipStrokeWidth = 0f
            chip.setEnsureMinTouchTargetSize(false)
            chipGroup.addView(chip)
        }
    }

    private fun openUrl(url: String) {
        try {
            val fullUrl = if (!url.startsWith("http")) "https://$url" else url
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl)))
        } catch (e: Exception) {
            showError("Could not open link")
        }
    }

    private fun loadUserPosts() {
        lifecycleScope.launch {
            try {
                val result = if (viewedUserId != null) {
                    projectRepository.getCurrentUserProjects()
                } else {
                    projectRepository.getCurrentUserProjects()
                }

                result
                    .onSuccess { projects ->
                        postList.clear()
                        postList.addAll(projects)
                        adapter.notifyDataSetChanged()

                        // Update stats and badge
                        projectsCount.text = projects.size.toString()
                        postsCountBadge.text = "${projects.size} project${if (projects.size != 1) "s" else ""}"

                        if (projects.isEmpty()) {
                            postsRecyclerView.visibility = View.GONE
                            emptyPostsLayout.visibility = View.VISIBLE
                        } else {
                            postsRecyclerView.visibility = View.VISIBLE
                            emptyPostsLayout.visibility = View.GONE
                        }
                    }
                    .onFailure { error ->
                        showError("Failed to load posts: ${error.message}")
                    }
            } catch (e: Exception) {
                showError("Failed to load posts: ${e.message}")
            }
        }
    }

    private fun navigateToCreateProfile() {
        try {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        } catch (e: Exception) {
            showError("Please create your profile first")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun deleteProject(project: ProjectIdea, position: Int) {
        lifecycleScope.launch {
            try {
                projectRepository.deleteProject(project.id.toString())
                    .onSuccess {
                        postList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, postList.size)

                        // Update counts
                        projectsCount.text = postList.size.toString()
                        postsCountBadge.text = "${postList.size} project${if (postList.size != 1) "s" else ""}"

                        if (postList.isEmpty()) {
                            postsRecyclerView.visibility = View.GONE
                            emptyPostsLayout.visibility = View.VISIBLE
                        }

                        Toast.makeText(requireContext(), "Project deleted", Toast.LENGTH_SHORT).show()
                    }
                    .onFailure { error ->
                        showError("Failed to delete project: ${error.message}")
                    }
            } catch (e: Exception) {
                showError("Error deleting project: ${e.message}")
            }
        }
    }
}
