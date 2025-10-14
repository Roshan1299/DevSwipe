package com.first.projectswipe.presentation.ui.projects

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
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.ProjectCreateRequest
import com.first.projectswipe.network.dto.UpdateProjectRequest
import com.first.projectswipe.network.dto.CollaborationCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



import com.google.android.flexbox.FlexboxLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateProjectIdeaFragment : Fragment() {

    @Inject
    lateinit var apiService: ApiService

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
    private lateinit var loadingProgressBar: ProgressBar
    
    // Collaboration Request fields
    private lateinit var projectIdeaFields: LinearLayout
    private lateinit var collabRequestFields: LinearLayout
    private lateinit var collabTitleEditText: EditText
    private lateinit var collabDescriptionEditText: EditText
    private lateinit var skillsNeededEditText: EditText
    private lateinit var timeCommitmentEditText: EditText
    private lateinit var teamSizeEditText: EditText



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

    private fun loadProjectForEditing(projectId: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            loadingProgressBar.visibility = View.VISIBLE
            try {
                val response = apiService.getProject(projectId)
                if (response.isSuccessful) {
                    val project = response.body()
                    if (project != null) {
                        projectTitleEditText.setText(project.title)
                        previewDescriptionEditText.setText(project.previewDescription)
                        fullDescriptionEditText.setText(project.fullDescription)
                        githubLinkEditText.setText(project.githubLink)
                        selectedTags.clear()
                        selectedTags.addAll(project.tags)
                        updateTagsDisplay()
                        val difficultyIndex = difficultyLevels.indexOf(project.difficulty)
                        if (difficultyIndex != -1) {
                            currentDifficultyIndex = difficultyIndex
                            updateDifficultyDisplay()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateProjectIdeaFragment", "Error loading project: $errorBody")
                    handleError(errorBody)
                }
            } catch (e: Exception) {
                Log.e("CreateProjectIdeaFragment", "Error loading project", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loadingProgressBar.visibility = View.GONE
            }
        }
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
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        
        // Collaboration Request views
        projectIdeaFields = view.findViewById(R.id.projectIdeaFields)
        collabRequestFields = view.findViewById(R.id.collabRequestFields)
        collabTitleEditText = view.findViewById(R.id.collabTitleEditText)
        collabDescriptionEditText = view.findViewById(R.id.collabDescriptionEditText)
        skillsNeededEditText = view.findViewById(R.id.skillsNeededEditText)
        timeCommitmentEditText = view.findViewById(R.id.timeCommitmentEditText)
        teamSizeEditText = view.findViewById(R.id.teamSizeEditText)

        updateDifficultyDisplay()
    }

    private fun handleError(errorBody: String?) {
        if (errorBody != null) {
            try {
                val errorResponse = com.google.gson.Gson().fromJson(errorBody, Map::class.java)
                val message = errorResponse["message"] as? String
                if (message != null) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    return
                }
            } catch (e: Exception) {
                Log.e("CreateProjectIdeaFragment", "Error parsing error response", e)
            }
        }
        Toast.makeText(context, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
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
        // Only allow adding tags when on the Project Idea tab
        if (selectedTab == "Project Idea") {
            val tagText = tagsEditText.text.toString().trim()
            if (tagText.isNotEmpty()) {
                addTag(tagText)
                tagsEditText.text.clear()
            }
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
                if (editingProjectId == null) {
                    saveCollaborationRequest()
                } else {
                    updateCollaborationRequest()
                }
            }
        }
    }

    private fun updateProjectIdea() {
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

        val updateProjectRequest = UpdateProjectRequest(
            title = title,
            previewDescription = preview,
            fullDescription = full,
            githubLink = githubLink,
            tags = selectedTags,
            difficulty = selectedDifficulty
        )

        lifecycleScope.launch(Dispatchers.Main) {
            loadingProgressBar.visibility = View.VISIBLE
            try {
                val response = apiService.updateProject(editingProjectId!!, updateProjectRequest)
                if (response.isSuccessful) {
                    Log.d("CreateProjectIdeaFragment", "Project updated successfully: ${response.body()?.id}")
                    Toast.makeText(context, "Project updated!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateProjectIdeaFragment", "Error updating project: $errorBody")
                    handleError(errorBody)
                }
            } catch (e: Exception) {
                Log.e("CreateProjectIdeaFragment", "Error updating project", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loadingProgressBar.visibility = View.GONE
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
        
        // Show/hide the appropriate fields based on the selected tab
        if (tab == "Project Idea") {
            projectIdeaFields.visibility = View.VISIBLE
            collabRequestFields.visibility = View.GONE
        } else {
            projectIdeaFields.visibility = View.GONE
            collabRequestFields.visibility = View.VISIBLE
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

        val projectCreateRequest = ProjectCreateRequest(
            title = title,
            previewDescription = preview,
            fullDescription = full,
            githubLink = githubLink,
            tags = selectedTags,
            difficulty = selectedDifficulty
        )

        lifecycleScope.launch(Dispatchers.Main) {
            loadingProgressBar.visibility = View.VISIBLE
            try {
                val response = apiService.createProject(projectCreateRequest)
                if (response.isSuccessful) {
                    Log.d("CreateProjectIdeaFragment", "Project saved successfully: ${response.body()?.id}")
                    Toast.makeText(context, "Project saved!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateProjectIdeaFragment", "Error saving project: $errorBody")
                    handleError(errorBody)
                }
            } catch (e: Exception) {
                Log.e("CreateProjectIdeaFragment", "Error saving project", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loadingProgressBar.visibility = View.GONE
            }
        }
    }

    private fun saveCollaborationRequest() {
        val title = collabTitleEditText.text.toString().trim()
        val description = collabDescriptionEditText.text.toString().trim()
        val skillsNeededText = skillsNeededEditText.text.toString().trim()
        val timeCommitment = timeCommitmentEditText.text.toString().trim()
        val teamSizeText = teamSizeEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || skillsNeededText.isEmpty() || timeCommitment.isEmpty() || teamSizeText.isEmpty()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val teamSize = try {
            teamSizeText.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Please enter a valid team size", Toast.LENGTH_SHORT).show()
            return
        }

        if (teamSize <= 0) {
            Toast.makeText(context, "Team size must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Parse skills from comma-separated input
        val skillsNeeded = skillsNeededText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        if (skillsNeeded.isEmpty()) {
            Toast.makeText(context, "Please enter at least one skill needed", Toast.LENGTH_SHORT).show()
            return
        }

        val collaborationCreateRequest = CollaborationCreateRequest(
            projectTitle = title,
            description = description,
            skillsNeeded = skillsNeeded,
            timeCommitment = timeCommitment,
            teamSize = teamSize
        )

        lifecycleScope.launch(Dispatchers.Main) {
            loadingProgressBar.visibility = View.VISIBLE
            try {
                val response = apiService.createCollaboration(collaborationCreateRequest)
                if (response.isSuccessful) {
                    Log.d("CreateProjectIdeaFragment", "Collaboration request saved successfully: ${response.body()?.id}")
                    Toast.makeText(context, "Collaboration request saved!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateProjectIdeaFragment", "Error saving collaboration request: $errorBody")
                    handleError(errorBody)
                }
            } catch (e: Exception) {
                Log.e("CreateProjectIdeaFragment", "Error saving collaboration request", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loadingProgressBar.visibility = View.GONE
            }
        }
    }

    private fun updateCollaborationRequest() {
        // For now, we'll just show a toast that editing is not implemented for collab requests
        // You can implement this later if needed
        Toast.makeText(context, "Editing collaboration requests is not yet implemented", Toast.LENGTH_SHORT).show()
    }
}
