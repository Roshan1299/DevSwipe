package com.first.projectswipe.presentation.ui.onboarding

import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OnboardingSkillsFragment : Fragment() {

    private lateinit var stepIndicator: TextView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var skillsContainer: FlexboxLayout
    private lateinit var nextButton: Button
    private lateinit var skipButton: TextView
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val selectedSkills = mutableListOf<String>()
    private val maxSkills = 10
    private val minSkills = 3

    // Comprehensive skills list organized by category
    private val skillsCategories = mapOf(
        "Programming Languages" to listOf(
            "Python", "JavaScript", "Java", "Kotlin", "Swift", "C++", "C#", "Go", "Rust", "TypeScript"
        ),
        "Web Development" to listOf(
            "React", "Vue.js", "Angular", "Node.js", "HTML/CSS", "Express.js", "Next.js", "Laravel", "Django", "Flask"
        ),
        "Mobile Development" to listOf(
            "Android", "iOS", "Flutter", "React Native", "Xamarin", "Ionic"
        ),
        "Data & AI" to listOf(
            "Machine Learning", "Data Science", "TensorFlow", "PyTorch", "SQL", "MongoDB", "PostgreSQL", "Data Analysis"
        ),
        "Cloud & DevOps" to listOf(
            "AWS", "Azure", "Google Cloud", "Docker", "Kubernetes", "CI/CD", "Jenkins", "Git"
        ),
        "Design & UI/UX" to listOf(
            "UI/UX Design", "Figma", "Adobe XD", "Photoshop", "Sketch", "Prototyping"
        )
    )

    // Flatten all skills for easy access
    private val allSkills = skillsCategories.values.flatten()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
        setupSkillsDisplay()
        updateNextButtonState()
    }

    private fun initializeViews(view: View) {
        stepIndicator = view.findViewById(R.id.stepIndicator)
        titleText = view.findViewById(R.id.titleText)
        subtitleText = view.findViewById(R.id.subtitleText)
        skillsContainer = view.findViewById(R.id.skillsContainer)
        nextButton = view.findViewById(R.id.nextButton)
        skipButton = view.findViewById(R.id.skipButton)
        progressBar = view.findViewById(R.id.progressBar)

        // Set initial text
        stepIndicator.text = "Step 1 of 2"
        titleText.text = "Select your skills"
        subtitleText.text = "Choose at least $minSkills skills that you're proficient in"
    }

    private fun setupClickListeners() {
        nextButton.setOnClickListener {
            if (selectedSkills.size >= minSkills) {
                saveSkillsAndProceed()
            } else {
                Toast.makeText(
                    context,
                    "Please select at least $minSkills skills",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        skipButton.setOnClickListener {
            // Navigate directly to interests without saving skills
            findNavController().navigate(R.id.action_onboardingSkillsFragment_to_onboardingInterestsFragment)
        }
    }

    private fun setupSkillsDisplay() {
        skillsContainer.removeAllViews()

        // Add category headers and skills
        skillsCategories.forEach { (category, skills) ->
            // Add category header
            addCategoryHeader(category)

            // Add skills in this category
            skills.forEach { skill ->
                skillsContainer.addView(createSkillChip(skill))
            }
        }
    }

    private fun addCategoryHeader(categoryName: String) {
        val headerView = LayoutInflater.from(context)
            .inflate(R.layout.item_category_header, skillsContainer, false)
        headerView.findViewById<TextView>(R.id.categoryText).text = categoryName
        skillsContainer.addView(headerView)
    }

    private fun createSkillChip(skill: String): View {
        val isSelected = selectedSkills.contains(skill)
        val layoutRes = if (isSelected) R.layout.chip_skill_selected else R.layout.chip_skill_unselected

        val chipView = LayoutInflater.from(context).inflate(layoutRes, skillsContainer, false)
        val chipText = chipView.findViewById<TextView>(R.id.chipText)
        chipText.text = skill

        chipView.setOnClickListener {
            toggleSkill(skill)
        }

        return chipView
    }

    private fun toggleSkill(skill: String) {
        if (selectedSkills.contains(skill)) {
            selectedSkills.remove(skill)
        } else {
            if (selectedSkills.size < maxSkills) {
                selectedSkills.add(skill)
            } else {
                Toast.makeText(
                    context,
                    "Maximum $maxSkills skills allowed",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        // Refresh the display
        setupSkillsDisplay()
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        val hasMinimumSkills = selectedSkills.size >= minSkills
        nextButton.isEnabled = hasMinimumSkills
        nextButton.alpha = if (hasMinimumSkills) 1.0f else 0.6f

        // Update subtitle with count
        subtitleText.text = if (selectedSkills.isEmpty()) {
            "Choose at least $minSkills skills that you're proficient in"
        } else {
            "${selectedSkills.size} selected • Choose at least $minSkills skills"
        }
    }

    private fun saveSkillsAndProceed() {
        val currentUser = auth.currentUser ?: return

        showLoading(true)

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // Update user document with selected skills
                db.collection("users").document(currentUser.uid)
                    .update("skills", selectedSkills)
                    .await()

                showLoading(false)

                // Navigate to interests selection
                findNavController().navigate(R.id.action_onboardingSkillsFragment_to_onboardingInterestsFragment)

            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    context,
                    "Failed to save skills: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        nextButton.isEnabled = !isLoading
        skipButton.isEnabled = !isLoading

        // Disable all skill chips during loading
        for (i in 0 until skillsContainer.childCount) {
            skillsContainer.getChildAt(i).isEnabled = !isLoading
        }
    }
}