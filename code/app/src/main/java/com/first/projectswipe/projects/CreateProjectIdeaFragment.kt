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
import com.first.projectswipe.models.ProjectIdea
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostFragment : Fragment() {

    private lateinit var projectIdeaTab: TextView
    private lateinit var collabRequestTab: TextView
    private lateinit var projectTitleEditText: EditText
    private lateinit var previewDescriptionEditText: EditText
    private lateinit var fullDescriptionEditText: EditText
    private lateinit var tagsEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var closeButton: ImageView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var selectedTab: String = "Project Idea"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        projectIdeaTab = view.findViewById(R.id.projectIdeaTab)
        collabRequestTab = view.findViewById(R.id.collabRequestTab)
        projectTitleEditText = view.findViewById(R.id.projectTitleEditText)
        previewDescriptionEditText = view.findViewById(R.id.previewDescriptionEditText)
        fullDescriptionEditText = view.findViewById(R.id.fullDescriptionEditText)
        tagsEditText = view.findViewById(R.id.projectTagsEditText)
        saveButton = view.findViewById(R.id.saveProjectButton)
        closeButton = view.findViewById(R.id.closeButton)

        setTabSelected("Project Idea")

        projectIdeaTab.setOnClickListener {
            setTabSelected("Project Idea")
        }
        collabRequestTab.setOnClickListener {
            setTabSelected("Collaboration Request")
        }

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

        return view
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

    private fun saveProjectIdea() {
        val title = projectTitleEditText.text.toString().trim()
        val preview = previewDescriptionEditText.text.toString().trim()
        val full = fullDescriptionEditText.text.toString().trim()
        val tags = tagsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

        if (title.isEmpty() || preview.isEmpty() || full.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser ?: return
        val newDocRef = db.collection("project_ideas").document()
        val project = ProjectIdea(
            id = newDocRef.id,
            title = title,
            previewDescription = preview,
            fullDescription = full,
            createdBy = currentUser.uid,
            tags = tags
        )

        newDocRef.set(project)
            .addOnSuccessListener {
                Toast.makeText(context, "Project saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}