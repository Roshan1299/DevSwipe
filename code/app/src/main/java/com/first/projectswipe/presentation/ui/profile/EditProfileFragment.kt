package com.first.projectswipe.presentation.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.databinding.FragmentEditProfileBinding
import com.first.projectswipe.network.dto.UpdateUserRequest
import com.first.projectswipe.network.dto.UserProfileResponse
import com.first.projectswipe.presentation.ui.auth.AuthManager
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager

    private var selectedImageUri: Uri? = null
    private var newProfilePictureUrl: String? = null
    private val IMAGE_PICK_CODE = 1010

    private val skills = mutableListOf<String>()
    private val interests = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupInputListeners()
        loadUserData()
        observeViewModel()
        updateCounts()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editImageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.editProfileImageView.setOnClickListener {
            pickImageFromGallery()
        }

        binding.saveChangesButton.setOnClickListener {
            saveProfile()
        }

        binding.addSkillButton.setOnClickListener {
            val skill = binding.addSkillInput.text.toString().trim()
            if (skill.isNotEmpty()) {
                addSkillChip(skill)
                binding.addSkillInput.setText("")
            }
        }

        binding.addInterestButton.setOnClickListener {
            val interest = binding.addInterestInput.text.toString().trim()
            if (interest.isNotEmpty()) {
                addInterestChip(interest)
                binding.addInterestInput.setText("")
            }
        }
    }

    private fun setupInputListeners() {
        binding.addSkillInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val skill = binding.addSkillInput.text.toString().trim()
                if (skill.isNotEmpty()) {
                    addSkillChip(skill)
                    binding.addSkillInput.setText("")
                }
                true
            } else false
        }

        binding.addInterestInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val interest = binding.addInterestInput.text.toString().trim()
                if (interest.isNotEmpty()) {
                    addInterestChip(interest)
                    binding.addInterestInput.setText("")
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
                setTextColor(Color.parseColor("#007AFF"))
                chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#E8F2FF"))
                closeIconTint = ColorStateList.valueOf(Color.parseColor("#007AFF"))
                chipStrokeWidth = 0f
                setEnsureMinTouchTargetSize(false)
                setOnCloseIconClickListener {
                    skills.remove(skill)
                    binding.skillsChipGroup.removeView(this)
                    updateCounts()
                }
            }
            binding.skillsChipGroup.addView(chip)
            updateCounts()
        }
    }

    private fun addInterestChip(interest: String) {
        if (!interests.contains(interest)) {
            interests.add(interest)
            val chip = Chip(requireContext()).apply {
                text = interest
                isCloseIconVisible = true
                setTextColor(Color.parseColor("#34C759"))
                chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#E8F8ED"))
                closeIconTint = ColorStateList.valueOf(Color.parseColor("#34C759"))
                chipStrokeWidth = 0f
                setEnsureMinTouchTargetSize(false)
                setOnCloseIconClickListener {
                    interests.remove(interest)
                    binding.interestsChipGroup.removeView(this)
                    updateCounts()
                }
            }
            binding.interestsChipGroup.addView(chip)
            updateCounts()
        }
    }

    private fun updateCounts() {
        binding.skillsCountLabel.text = "${skills.size} added"
        binding.interestsCountLabel.text = "${interests.size} added"
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.editProfileImageView.setImageURI(selectedImageUri)
            selectedImageUri?.let {
                val inputStream = requireActivity().contentResolver.openInputStream(it)
                val file = File(requireContext().cacheDir, "temp_image.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.uploadProfilePicture(it, file)
            }
        }
    }

    private fun loadUserData() {
        viewModel.getUserProfile()
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner, Observer<UserProfileResponse> { user ->
            if (user != null) {
                binding.nameInput.setText(user.name)
                binding.universityInput.setText(user.university)
                binding.bioInput.setText(user.bio)
                binding.githubInput.setText(user.githubUrl)
                binding.linkedinInput.setText(user.linkedinUrl)

                if (!user.profileImageUrl.isNullOrEmpty()) {
                    Picasso.get().load(user.profileImageUrl).into(binding.editProfileImageView)
                }

                // Clear existing chips before adding
                skills.clear()
                interests.clear()
                binding.skillsChipGroup.removeAllViews()
                binding.interestsChipGroup.removeAllViews()

                user.skills?.forEach { addSkillChip(it) }
                user.interests?.forEach { addInterestChip(it) }
            }
        })

        viewModel.uploadUrl.observe(viewLifecycleOwner, Observer { url ->
            if (url != null) {
                newProfilePictureUrl = url
            }
        })
    }

    private fun saveProfile() {
        val name = binding.nameInput.text.toString().trim()
        val university = binding.universityInput.text.toString().trim()
        val bio = binding.bioInput.text.toString().trim()
        val github = binding.githubInput.text.toString().trim()
        val linkedin = binding.linkedinInput.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }

        val updateUserRequest = UpdateUserRequest(
            name = name,
            university = university.ifEmpty { null },
            bio = bio.ifEmpty { null },
            profileImageUrl = newProfilePictureUrl ?: viewModel.userProfile.value?.profileImageUrl,
            githubUrl = github.ifEmpty { null },
            linkedinUrl = linkedin.ifEmpty { null },
            skills = skills,
            interests = interests
        )

        viewModel.updateUser(updateUserRequest)
        Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
