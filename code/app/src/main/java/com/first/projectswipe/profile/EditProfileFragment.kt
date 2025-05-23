package com.first.projectswipe.profile

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
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private lateinit var profileImageView: ShapeableImageView
    private lateinit var nameInput: EditText
    private lateinit var universityInput: EditText
    private lateinit var bioInput: EditText
    private lateinit var skillInput: EditText
    private lateinit var skillChipGroup: ChipGroup
    private lateinit var saveButton: Button

    private val skills = mutableListOf<String>()
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

        profileImageView = view.findViewById(R.id.editProfileImageView)
        nameInput = view.findViewById(R.id.nameInput)
        universityInput = view.findViewById(R.id.universityInput)
        bioInput = view.findViewById(R.id.bioInput)
        skillInput = view.findViewById(R.id.addSkillInput)
        skillChipGroup = view.findViewById(R.id.skillsChipGroup)
        saveButton = view.findViewById(R.id.saveProfileButton)

        loadUserData()
        setupSkillInput()
        setupSaveButton()

        profileImageView.setOnClickListener {
            requestImagePermission()
        }

        return view
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
            nameInput.setText(doc.getString("name") ?: "")
            universityInput.setText(doc.getString("university") ?: "")
            bioInput.setText(doc.getString("bio") ?: "")
            val url = doc.getString("profileImageUrl")
            if (!url.isNullOrEmpty()) {
                Picasso.get().load(url).into(profileImageView)
            }
            (doc["skills"] as? List<*>)?.forEach {
                if (it is String) addSkillChip(it)
            }
        }
    }

    private fun setupSkillInput() {
        skillInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val skill = skillInput.text.toString().trim()
                if (skill.isNotEmpty()) {
                    addSkillChip(skill)
                    skillInput.setText("")
                }
                true
            } else false
        }
    }

    private fun addSkillChip(skill: String) {
        if (!skills.contains(skill)) {
            skills.add(skill)
            val chip = Chip(requireContext())
            chip.text = skill
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                skills.remove(skill)
                skillChipGroup.removeView(chip)
            }
            skillChipGroup.addView(chip)
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            saveButton.isEnabled = false
            saveButton.text = "Saving..."

            val updatedData = mapOf(
                "name" to nameInput.text.toString(),
                "university" to universityInput.text.toString(),
                "bio" to bioInput.text.toString(),
                "skills" to skills
            )

            db.collection("users").document(userId).update(updatedData).addOnSuccessListener {
                if (selectedImageUri != null) {
                    uploadProfileImage(selectedImageUri!!)
                } else {
                    Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                    navigateToProfile()
                }
            }.addOnFailureListener {
                saveButton.isEnabled = true
                saveButton.text = "Save Changes"
                Toast.makeText(context, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadProfileImage(uri: Uri) {
        val ref = storage.reference.child("profile_pics/$userId.jpg")
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                db.collection("users").document(userId)
                    .update("profileImageUrl", downloadUrl.toString())
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile photo updated!", Toast.LENGTH_SHORT).show()
                        navigateToProfile()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Image URL update failed", Toast.LENGTH_SHORT).show()
                        saveButton.isEnabled = true
                        saveButton.text = "Save Changes"
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
            saveButton.isEnabled = true
            saveButton.text = "Save Changes"
        }
    }

    private fun navigateToProfile() {
        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
    }


}
