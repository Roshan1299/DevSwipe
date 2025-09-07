package com.first.projectswipe.presentation.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.databinding.FragmentEditProfileBinding
import com.first.projectswipe.network.dto.UpdateUserRequest
import com.first.projectswipe.presentation.ui.auth.AuthManager
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject
import com.first.projectswipe.network.dto.UserProfileResponse

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
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editImageButton.setOnClickListener {
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
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.blue_2196F3)
                closeIconTint = ContextCompat.getColorStateList(requireContext(), android.R.color.white)
                setOnCloseIconClickListener {
                    skills.remove(skill)
                    binding.skillsChipGroup.removeView(this)
                }
            }
            binding.skillsChipGroup.addView(chip)
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
                    binding.interestsChipGroup.removeView(this)
                }
            }
            binding.interestsChipGroup.addView(chip)
        }
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
                if (!user.profileImageUrl.isNullOrEmpty()) {
                    Picasso.get().load(user.profileImageUrl).into(binding.editProfileImageView)
                }
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
        val name = binding.nameInput.text.toString()
        val university = binding.universityInput.text.toString()
        val bio = binding.bioInput.text.toString()

        val updateUserRequest = UpdateUserRequest(
            name = name,
            university = university,
            bio = bio,
            profileImageUrl = newProfilePictureUrl ?: viewModel.userProfile.value?.profileImageUrl,
            skills = skills,
            interests = interests
        )

        viewModel.updateUser(updateUserRequest)
        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}