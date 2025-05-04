package com.first.projectswipe.projects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea

class CreateProjectIdeaFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var tagsEditText: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_project_idea, container, false)

        titleEditText = view.findViewById(R.id.projectTitleEditText)
        descriptionEditText = view.findViewById(R.id.projectDescriptionEditText)
        tagsEditText = view.findViewById(R.id.projectTagsEditText)
        saveButton = view.findViewById(R.id.saveProjectButton)

        saveButton.setOnClickListener {
            saveProjectIdea()
        }

        return view
    }

    private fun saveProjectIdea() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val tags = tagsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

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
                titleEditText.text.clear()
                descriptionEditText.text.clear()
                tagsEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e("CreateProject", "Error saving project", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
