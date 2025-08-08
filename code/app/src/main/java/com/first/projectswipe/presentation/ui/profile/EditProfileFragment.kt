package com.first.projectswipe.presentation.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private lateinit var profileImageView: ShapeableImageView
    private lateinit var editImageButton: ImageButton
    private lateinit var nameInput: TextInputEditText
    private lateinit var universityInput: TextInputEditText
    private lateinit var bioInput: TextInputEditText
    private lateinit var addSkillInput: TextInputEditText
    private lateinit var addInterestInput: TextInputEditText
    private lateinit var skillsChipGroup: ChipGroup
    private lateinit var interestsChipGroup: ChipGroup
    private lateinit var addSkillButton: ImageButton
    private lateinit var addInterestButton: ImageButton
    private lateinit var saveChangesButton: Button
    private lateinit var backButton: ImageButton

    private val skills = mutableListOf<String>()
    private val interests = mutableListOf<String>()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1010
    private val PERMISSION_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        initializeViews(view)
        setupClickListeners()
        setupInputListeners()
        loadUserData()

        return view
    }

    private fun initializeViews(view: View) {
        profileImageView = view.findViewById(R.id.editProfileImageView)
        editImageButton = view.findViewById(R.id.editImageButton)
        nameInput = view.findViewById(R.id.nameInput)
        universityInput = view.findViewById(R.id.universityInput)
        bioInput = view.findViewById(R.id.bioInput)
        addSkillInput = view.findViewById(R.id.addSkillInput)
        addInterestInput = view.findViewById(R.id.addInterestInput)
        skillsChipGroup = view.findViewById(R.id.skillsChipGroup)
        interestsChipGroup = view.findViewById(R.id.interestsChipGroup)
        addSkillButton = view.findViewById(R.id.addSkillButton)
        addInterestButton = view.findViewById(R.id.addInterestButton)
        saveChangesButton = view.findViewById(R.id.saveChangesButton)
        backButton = view.findViewById(R.id.backButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        editImageButton.setOnClickListener {
            requestImagePermission()
        }

        profileImageView.setOnClickListener {
            requestImagePermission()
        }

        addSkillButton.setOnClickListener {
            val skill = addSkillInput.text.toString().trim()
            if (skill.isNotEmpty()) {
                addSkillChip(skill)
                addSkillInput.setText("")
            }
        }

        addInterestButton.setOnClickListener {
            val interest = addInterestInput.text.toString().trim()
            if (interest.isNotEmpty()) {
                addInterestChip(interest)
                addInterestInput.setText("")
            }
        }

        saveChangesButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun setupInputListeners() {
        addSkillInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val skill = addSkillInput.text.toString().trim()
                if (skill.isNotEmpty()) {
                    addSkillChip(skill)
                    addSkillInput.setText("")
                }
                true
            } else false
        }

        addInterestInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val interest = addInterestInput.text.toString().trim()
                if (interest.isNotEmpty()) {
                    addInterestChip(interest)
                    addInterestInput.setText("")
                }
                true
            } else false
        }
    }

    private fun addSkillChip(skill: String) {
        if (!skills.contains(skill)) {
            skills.add(skill)
            val chip = Chip(requireContext()).apply {
                text = skill
                isCloseIconVisible = true
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.blue_2196F3)
                closeIconTint = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
                setOnCloseIconClickListener {
                    skills.remove(skill)
                    skillsChipGroup.removeView(this)
                }
            }
            skillsChipGroup.addView(chip)
        }
    }

    private fun addInterestChip(interest: String) {
        if (!interests.contains(interest)) {
            interests.add(interest)
            val chip = Chip(requireContext()).apply {
                text = interest
                isCloseIconVisible = true
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.blue_2196F3)
                closeIconTint = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
                setOnCloseIconClickListener {
                    interests.remove(interest)
                    interestsChipGroup.removeView(this)
                }
            }
            interestsChipGroup.addView(chip)
        }
    }

    private fun requestImagePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), PERMISSION_CODE)
        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            profileImageView.setImageURI(selectedImageUri)
        }
    }

    private fun loadUserData() {
        db.collection("users").document(userId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                nameInput.setText(doc.getString("name") ?: "Alex Doe")
                universityInput.setText(doc.getString("university") ?: "Example University")
                bioInput.setText(doc.getString("bio") ?: "Computer science student. Passionate about programming and open source.")

                val url = doc.getString("profileImageUrl")
                if (!url.isNullOrEmpty()) {
                    Picasso.get().load(url).into(profileImageView)
                }

                // Load existing skills
                (doc["skills"] as? List<*>)?.forEach {
                    if (it is String && !skills.contains(it)) {
                        addSkillChip(it)
                    }
                }

                // Load existing interests
                (doc["interests"] as? List<*>)?.forEach {
                    if (it is String && !interests.contains(it)) {
                        addInterestChip(it)
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        saveChangesButton.isEnabled = false

        val updatedData = hashMapOf(
            "name" to nameInput.text.toString(),
            "university" to universityInput.text.toString(),
            "bio" to bioInput.text.toString(),
            "skills" to skills,
            "interests" to interests
        )

        db.collection("users").document(userId).update(updatedData as Map<String, Any>)
            .addOnSuccessListener {
                if (selectedImageUri != null) {
                    uploadProfileImage(selectedImageUri!!)
                } else {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
            .addOnFailureListener { exception ->
                saveChangesButton.isEnabled = true
                Toast.makeText(context, "Update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImage(uri: Uri) {
        val ref = storage.reference.child("profile_pics/$userId.jpg")
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                db.collection("users").document(userId)
                    .update("profileImageUrl", downloadUrl.toString())
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        saveChangesButton.isEnabled = true
                        Toast.makeText(context, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            saveChangesButton.isEnabled = true
            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }
}