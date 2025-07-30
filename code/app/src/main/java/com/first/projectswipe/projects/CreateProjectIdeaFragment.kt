package com.first.projectswipe.projects

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.google.android.flexbox.FlexboxLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreatePostFragment : Fragment() {

    private lateinit var projectIdeaTab: TextView
    private lateinit var collabRequestTab: TextView
    private lateinit var projectTitleEditText: EditText
    private lateinit var previewDescriptionEditText: EditText
    private lateinit var fullDescriptionEditText: EditText
    private lateinit var tagsEditText: EditText
    private lateinit var addTagButton: ImageView
    private lateinit var tagsContainer: FlexboxLayout
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

    // Tag management
    private val selectedTags = mutableListOf<String>()
    private val maxTags = 5
    private val maxTagLength = 20

    // Popular tech stack suggestions
    private val popularTags = listOf(
        "Web Dev",
        "Python",
        "AI/ML",
        "JavaScript",
        "Kotlin",
        "Data Science",
        "DevOps",
        "Docker",
        "C++"
    )

    // If editing an existing project, this will be set (via Bundle/navigation args)
    private var editingProjectId: String? = null

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
        setupTextLimits()
        setupTagInput()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Bottom navigation is already hidden by MainActivity's navigation listener
        updateTagsDisplay() // Initialize tags display

        // --- Start of the new logic for edit ---
        val bundleProjectId = arguments?.getString("projectId") ?: ""
        if (bundleProjectId.isNotBlank()) {
            editingProjectId = bundleProjectId
            loadProjectForEditing(editingProjectId!!)
        }
        // --- End of new logic for edit ---
    }

    private fun initializeViews(view: View) {
        projectIdeaTab = view.findViewById(R.id.projectIdeaTab)
        collabRequestTab = view.findViewById(R.id.collabRequestTab)
        projectTitleEditText = view.findViewById(R.id.projectTitleEditText)
        previewDescriptionEditText = view.findViewById(R.id.previewDescriptionEditText)
        fullDescriptionEditText = view.findViewById(R.id.fullDescriptionEditText)
        tagsEditText = view.findViewById(R.id.projectTagsEditText)
        addTagButton = view.findViewById(R.id.addTagButton)
        tagsContainer = view.findViewById(R.id.tagsContainer)
        githubLinkEditText = view.findViewById(R.id.githubLinkEditText)
        difficultyLevelContainer = view.findViewById(R.id.difficultyLevelContainer)
        difficultyLevelText = view.findViewById(R.id.difficultyLevelText)
        difficultyUpArrow = view.findViewById(R.id.difficultyUpArrow)
        difficultyDownArrow = view.findViewById(R.id.difficultyDownArrow)
        saveButton = view.findViewById(R.id.saveProjectButton)
        closeButton = view.findViewById(R.id.closeButton)

        updateDifficultyDisplay()
    }

    private fun setupTagInput() {
        // Add tag length filter
        tagsEditText.filters = arrayOf(InputFilter.LengthFilter(maxTagLength))

        addTagButton.setOnClickListener {
            addTagFromInput()
        }

        tagsEditText.setOnEditorActionListener { _, _, _ ->
            addTagFromInput()
            true
        }
    }

    private fun addTagFromInput() {
        val tagText = tagsEditText.text.toString().trim()
        if (tagText.isNotEmpty()) {
            addTag(tagText)
            tagsEditText.text.clear()
        }
    }

    private fun addTag(tag: String) {
        // Validate tag
        if (tag.length > maxTagLength) {
            Toast.makeText(context, "Tag is too long (max $maxTagLength characters)", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTags.size >= maxTags) {
            Toast.makeText(context, "Maximum $maxTags tags allowed", Toast.LENGTH_SHORT).show()
            return
        }

        // Only capitalize the first letter, preserve the rest of the user's input
        val normalizedTag = if (tag.isNotEmpty()) {
            tag.first().uppercase() + tag.drop(1)
        } else {
            tag
        }

        if (selectedTags.contains(normalizedTag)) {
            Toast.makeText(context, "Tag already added", Toast.LENGTH_SHORT).show()
            return
        }

        selectedTags.add(normalizedTag)
        updateTagsDisplay()
    }

    private fun removeTag(tag: String) {
        selectedTags.remove(tag)
        updateTagsDisplay()
    }

    private fun updateTagsDisplay() {
        tagsContainer.removeAllViews()

        // Add selected tags
        selectedTags.forEach { tag ->
            tagsContainer.addView(createSelectedTagChip(tag))
        }

        // Always show at least 8 popular tags (or all available if less than 8)
        // Filter out already selected tags and show up to 8 popular tags
        popularTags.filter { !selectedTags.contains(it) }
            .take(8)
            .forEach { tag ->
                tagsContainer.addView(createPopularTagChip(tag))
            }
    }

    private fun createPopularTagChip(tag: String): View {
        val chipView = LayoutInflater.from(context).inflate(R.layout.chip_popular_tag, tagsContainer, false)
        chipView.findViewById<TextView>(R.id.chipText).text = tag
        chipView.setOnClickListener {
            addTag(tag)
        }
        return chipView
    }

    private fun createSelectedTagChip(tag: String): View {
        val chipView = LayoutInflater.from(context).inflate(R.layout.chip_selected_tag, tagsContainer, false)
        chipView.findViewById<TextView>(R.id.chipText).text = tag
        chipView.findViewById<ImageView>(R.id.removeTagButton).setOnClickListener {
            removeTag(tag)
        }
        return chipView
    }

    private fun setupTextLimits() {
        // Limit preview description to 2 lines
        previewDescriptionEditText.maxLines = 3
        previewDescriptionEditText.filters = arrayOf(
            InputFilter.LengthFilter(100),
            object : InputFilter {
                override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ): CharSequence? {
                    val newText = dest.toString().substring(0, dstart) +
                            source.toString().substring(start, end) +
                            dest.toString().substring(dend)
                    return if (newText.split("\n").size > 2) "" else null
                }
            }
        )

        // Limit full description to 4 lines
        fullDescriptionEditText.filters = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val newText = dest.toString().substring(0, dstart) +
                        source.toString().substring(start, end) +
                        dest.toString().substring(dend)
                return if (newText.split("\n").size > 4) "" else null
            }
        })
        fullDescriptionEditText.maxLines = 4
    }

    private fun setupTabSelection() {
        setTabSelected("Project Idea")
        projectIdeaTab.setOnClickListener { setTabSelected("Project Idea") }
        collabRequestTab.setOnClickListener { setTabSelected("Collaboration Request") }
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

        difficultyLevelContainer.setOnClickListener {
            currentDifficultyIndex = (currentDifficultyIndex + 1) % difficultyLevels.size
            updateDifficultyDisplay()
        }
    }

    private fun updateDifficultyDisplay() {
        selectedDifficulty = difficultyLevels[currentDifficultyIndex]
        difficultyLevelText.text = selectedDifficulty
        difficultyUpArrow.alpha = if (currentDifficultyIndex > 0) 1.0f else 0.3f
        difficultyDownArrow.alpha = if (currentDifficultyIndex < difficultyLevels.size - 1) 1.0f else 0.3f
    }

    private fun setupClickListeners() {
        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        saveButton.setOnClickListener {
            if (selectedTab == "Project Idea") {
                if (editingProjectId == null) {
                    saveProjectIdea()
                } else {
                    updateProjectIdea()
                }
            } else {
                Toast.makeText(context, "Collaboration Request not implemented yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFocusListeners() {
        listOf(
            projectTitleEditText,
            previewDescriptionEditText,
            fullDescriptionEditText,
            tagsEditText,
            githubLinkEditText
        ).forEach { editText ->
            editText.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.postDelayed({ scrollToView(view) }, 200)
                }
            }
        }
    }

    private fun scrollToView(view: View) {
        val scrollView = this.view?.parent as? androidx.core.widget.NestedScrollView
        scrollView?.let {
            val extraPadding = when (view.id) {
                R.id.projectTitleEditText -> 150
                R.id.previewDescriptionEditText -> 200
                R.id.fullDescriptionEditText -> 250
                R.id.projectTagsEditText -> 300
                R.id.githubLinkEditText -> 350
                else -> 200
            }
            it.smoothScrollTo(0, view.top + extraPadding)
        }
    }

    private fun setTabSelected(tab: String) {
        selectedTab = tab
        projectIdeaTab.apply {
            background = ContextCompat.getDrawable(requireContext(),
                if (tab == "Project Idea") R.drawable.tab_selected_bg else R.drawable.tab_unselected_bg)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        collabRequestTab.apply {
            background = ContextCompat.getDrawable(requireContext(),
                if (tab == "Collaboration Request") R.drawable.tab_selected_bg else R.drawable.tab_unselected_bg)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    private fun isValidGitHubUrl(url: String): Boolean {
        return url.isEmpty() || url.startsWith("https://github.com/") || url.startsWith("http://github.com/")
    }

    private fun saveProjectIdea() {
        val title = projectTitleEditText.text.toString().trim()
        val preview = previewDescriptionEditText.text.toString().trim()
        val full = fullDescriptionEditText.text.toString().trim()
        val githubLink = githubLinkEditText.text.toString().trim()

        if (title.isEmpty() || preview.isEmpty() || full.isEmpty()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTags.isEmpty()) {
            Toast.makeText(context, "Please add at least one tag", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidGitHubUrl(githubLink)) {
            Toast.makeText(context, "Please enter a valid GitHub URL", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser ?: return
        val newDocRef = db.collection("project_ideas").document()

        Log.d("CreatePostFragment", "Saving project with ID: ${newDocRef.id}")

        // Get user's display name or email for createdByName
        val createdByName = currentUser.displayName ?: currentUser.email ?: "Anonymous"

        val projectData = mutableMapOf<String, Any>(
            "id" to newDocRef.id,
            "title" to title,
            "previewDescription" to preview,
            "fullDescription" to full,
            "createdBy" to currentUser.uid,
            "createdByName" to createdByName,
            "tags" to selectedTags,
            "difficulty" to selectedDifficulty,
            "timeline" to "",
            "createdAt" to FieldValue.serverTimestamp()
        )

        projectData["githubLink"] = githubLink

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                newDocRef.set(projectData).await()
                Log.d("CreatePostFragment", "Project saved successfully: ${newDocRef.id}")
                Toast.makeText(context, "Project saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                Log.e("CreatePostFragment", "Error saving project", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------- EDIT PROJECT LOGIC ----------
    private fun loadProjectForEditing(projectId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val doc = db.collection("project_ideas").document(projectId).get().await()
                if (doc.exists()) {
                    val data = doc.data
                    data?.let {
                        projectTitleEditText.setText(data["title"] as? String ?: "")
                        previewDescriptionEditText.setText(data["previewDescription"] as? String ?: "")
                        fullDescriptionEditText.setText(data["fullDescription"] as? String ?: "")
                        githubLinkEditText.setText(data["githubLink"] as? String ?: "")
                        val tags = (data["tags"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                        selectedTags.clear()
                        selectedTags.addAll(tags)
                        updateTagsDisplay()

                        // Set difficulty
                        val diff = data["difficulty"] as? String ?: "Beginner"
                        currentDifficultyIndex = difficultyLevels.indexOf(diff).takeIf { it != -1 } ?: 0
                        updateDifficultyDisplay()
                    }
                    // Update UI to reflect that we are editing (optional)
                    saveButton.text = getString(R.string.update_project) // Update button label
                } else {
                    Toast.makeText(context, "Project not found!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load project: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProjectIdea() {
        val projectId = editingProjectId ?: return
        val title = projectTitleEditText.text.toString().trim()
        val preview = previewDescriptionEditText.text.toString().trim()
        val full = fullDescriptionEditText.text.toString().trim()
        val githubLink = githubLinkEditText.text.toString().trim()

        if (title.isEmpty() || preview.isEmpty() || full.isEmpty()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedTags.isEmpty()) {
            Toast.makeText(context, "Please add at least one tag", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isValidGitHubUrl(githubLink)) {
            Toast.makeText(context, "Please enter a valid GitHub URL", Toast.LENGTH_SHORT).show()
            return
        }
        val currentUser = auth.currentUser ?: return
        val updatedData = mutableMapOf<String, Any>(
            "title" to title,
            "previewDescription" to preview,
            "fullDescription" to full,
            "tags" to selectedTags,
            "difficulty" to selectedDifficulty,
            "githubLink" to githubLink
        )

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                db.collection("project_ideas").document(projectId).update(updatedData as Map<String, Any>).await()
                Toast.makeText(context, "Project updated!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } catch (e: Exception) {
                Toast.makeText(context, "Error updating: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
