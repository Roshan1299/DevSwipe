package com.first.projectswipe.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateProjectIdeaFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var tagsEditText: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var editingProjectId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_project_idea, container, false)

        titleEditText = view.findViewById(R.id.projectTitleEditText)
        descriptionEditText = view.findViewById(R.id.projectDescriptionEditText)
        tagsEditText = view.findViewById(R.id.projectTagsEditText)
        saveButton = view.findViewById(R.id.saveProjectButton)

        // Get the projectId from Bundle arguments (passed from ProfilePostAdapter)
        editingProjectId = arguments?.getString("projectId")
        if (!editingProjectId.isNullOrEmpty()) {
            loadProjectForEdit(editingProjectId!!)
        }

        saveButton.setOnClickListener {
            if (editingProjectId.isNullOrEmpty()) {
                saveNewProjectIdea()
            } else {
                updateExistingProject(editingProjectId!!)
            }
        }

        return view
    }

    private fun loadProjectForEdit(projectId: String) {
        db.collection("project_ideas").document(projectId).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val project = doc.toObject(ProjectIdea::class.java)
                    project?.let {
                        titleEditText.setText(it.title)
                        descriptionEditText.setText(it.description)
                        tagsEditText.setText(it.tags.joinToString(", "))
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load project: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveNewProjectIdea() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val tags = tagsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser ?: return
        val newDocRef = db.collection("project_ideas").document()
        val project = ProjectIdea(
            id = newDocRef.id,
            title = title,
            description = description,
            createdBy = currentUser.uid,
            tags = tags
        )

        newDocRef.set(project)
            .addOnSuccessListener {
                Toast.makeText(context, "Project saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Log.e("CreateProject", "Error saving project", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateExistingProject(projectId: String) {
        val updatedData = mapOf(
            "title" to titleEditText.text.toString().trim(),
            "description" to descriptionEditText.text.toString().trim(),
            "tags" to tagsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        )

        db.collection("project_ideas").document(projectId)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(context, "Project updated!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
