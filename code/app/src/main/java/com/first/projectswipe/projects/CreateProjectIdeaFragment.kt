package com.first.projectswipe.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostFragment : Fragment() {

    private lateinit var projectIdeaTab: TextView
    private lateinit var collabRequestTab: TextView
    private lateinit var projectTitleEditText: EditText
    private lateinit var previewDescriptionEditText: EditText
    private lateinit var fullDescriptionEditText: EditText
    private lateinit var tagsEditText: EditText
    private lateinit var githubLinkEditText: EditText
    private lateinit var difficultyLevelContainer: View
    private lateinit var difficultyLevelText: TextView
    private lateinit var difficultyUpArrow: ImageView
    private lateinit var difficultyDownArrow: ImageView
    private lateinit var saveButton: Button
    private lateinit var closeButton: ImageView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var selectedTab: String = "Project Idea"
    private var selectedDifficulty: String = "Beginner"
    private val difficultyLevels = listOf("Beginner", "Intermediate", "Advanced", "Expert")
    private var currentDifficultyIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        initializeViews(view)
        setupTabSelection()
        setupDifficultySelection()
        setupClickListeners()
        setupFocusListeners()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Hide bottom navigation when this fragment is shown
        activity?.findViewById<View>(R.id.bottomNavigationView)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when leaving this fragment
        activity?.findViewById<View>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
    }

    private fun initializeViews(view: View) {
        projectIdeaTab = view.findViewById(R.id.projectIdeaTab)
        collabRequestTab = view.findViewById(R.id.collabRequestTab)
        projectTitleEditText = view.findViewById(R.id.projectTitleEditText)
        previewDescriptionEditText = view.findViewById(R.id.previewDescriptionEditText)
        fullDescriptionEditText = view.findViewById(R.id.fullDescriptionEditText)
        tagsEditText = view.findViewById(R.id.projectTagsEditText)
        githubLinkEditText = view.findViewById(R.id.githubLinkEditText)
        difficultyLevelContainer = view.findViewById(R.id.difficultyLevelContainer)
        difficultyLevelText = view.findViewById(R.id.difficultyLevelText)
        difficultyUpArrow = view.findViewById(R.id.difficultyUpArrow)
        difficultyDownArrow = view.findViewById(R.id.difficultyDownArrow)
        saveButton = view.findViewById(R.id.saveProjectButton)
        closeButton = view.findViewById(R.id.closeButton)

        // Set initial difficulty level
        updateDifficultyDisplay()
    }

    private fun setupTabSelection() {
        setTabSelected("Project Idea")

        projectIdeaTab.setOnClickListener {
            setTabSelected("Project Idea")
        }
        collabRequestTab.setOnClickListener {
            setTabSelected("Collaboration Request")
        }
    }

    private fun setupDifficultySelection() {
        difficultyUpArrow.setOnClickListener {
            if (currentDifficultyIndex > 0) {
                currentDifficultyIndex--
                updateDifficultyDisplay()
            }
        }

        difficultyDownArrow.setOnClickListener {
            if (currentDifficultyIndex < difficultyLevels.size - 1) {
                currentDifficultyIndex++
                updateDifficultyDisplay()
            }
        }

        // Make the entire container clickable to cycle through difficulties
        difficultyLevelContainer.setOnClickListener {
            currentDifficultyIndex = (currentDifficultyIndex + 1) % difficultyLevels.size
            updateDifficultyDisplay()
        }
    }

    private fun updateDifficultyDisplay() {
        selectedDifficulty = difficultyLevels[currentDifficultyIndex]
        difficultyLevelText.text = selectedDifficulty

        // Update arrow visibility/opacity based on position
        difficultyUpArrow.alpha = if (currentDifficultyIndex > 0) 1.0f else 0.3f
        difficultyDownArrow.alpha = if (currentDifficultyIndex < difficultyLevels.size - 1) 1.0f else 0.3f
    }

    private fun setupClickListeners() {
        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        saveButton.setOnClickListener {
            if (selectedTab == "Project Idea") {
                saveProjectIdea()
            } else {
                // Implement Collaboration Request save logic
                Toast.makeText(context, "Collaboration Request not implemented yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFocusListeners() {
        val editTexts = listOf(
            projectTitleEditText,
            previewDescriptionEditText,
            fullDescriptionEditText,
            tagsEditText,
            githubLinkEditText
        )

        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    // Scroll to the focused field with a small delay to ensure keyboard is visible
                    view.postDelayed({
                        scrollToView(view)
                    }, 200)
                }
            }
        }
    }

    private fun scrollToView(view: View) {
        val scrollView = this.view?.parent as? androidx.core.widget.NestedScrollView
        scrollView?.let { sv ->
            // Calculate appropriate padding based on the field
            val extraPadding = when (view.id) {
                R.id.projectTitleEditText -> 150
                R.id.previewDescriptionEditText -> 200
                R.id.fullDescriptionEditText -> 250
                R.id.projectTagsEditText -> 300
                R.id.githubLinkEditText -> 350
                else -> 200
            }

            // Scroll to position that keeps the field visible above the keyboard
            val scrollY = view.top + extraPadding
            sv.smoothScrollTo(0, scrollY.coerceAtLeast(0))
        }
    }

    private fun setTabSelected(tab: String) {
        selectedTab = tab
        if (tab == "Project Idea") {
            // Set Project Idea tab as selected (white background)
            projectIdeaTab.background = ContextCompat.getDrawable(requireContext(), R.drawable.tab_selected_bg)
            projectIdeaTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            // Set Collaboration Request tab as unselected (transparent/light blue)
            collabRequestTab.background = ContextCompat.getDrawable(requireContext(), R.drawable.tab_unselected_bg)
            collabRequestTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            // Set Project Idea tab as unselected (transparent/light blue)
            projectIdeaTab.background = ContextCompat.getDrawable(requireContext(), R.drawable.tab_unselected_bg)
            projectIdeaTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            // Set Collaboration Request tab as selected (white background)
            collabRequestTab.background = ContextCompat.getDrawable(requireContext(), R.drawable.tab_selected_bg)
            collabRequestTab.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    private fun isValidGitHubUrl(url: String): Boolean {
        if (url.isEmpty()) return true // Optional field, empty is valid
        return url.startsWith("https://github.com/") || url.startsWith("http://github.com/")
    }

    private fun saveProjectIdea() {
        val title = projectTitleEditText.text.toString().trim()
        val preview = previewDescriptionEditText.text.toString().trim()
        val full = fullDescriptionEditText.text.toString().trim()
        val tags = tagsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val githubLink = githubLinkEditText.text.toString().trim()

        if (title.isEmpty() || preview.isEmpty() || full.isEmpty()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidGitHubUrl(githubLink)) {
            Toast.makeText(context, "Please enter a valid GitHub URL (https://github.com/...)", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser ?: return
        val newDocRef = db.collection("project_ideas").document()

        // Create project data map with optional GitHub link
        val projectData = mutableMapOf<String, Any>(
            "id" to newDocRef.id,
            "title" to title,
            "previewDescription" to preview,
            "fullDescription" to full,
            "createdBy" to currentUser.uid,
            "tags" to tags,
            "difficulty" to selectedDifficulty,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        // Add GitHub link only if it's not empty
        if (githubLink.isNotEmpty()) {
            projectData["githubLink"] = githubLink
        }

        newDocRef.set(projectData)
            .addOnSuccessListener {
                Toast.makeText(context, "Project saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}