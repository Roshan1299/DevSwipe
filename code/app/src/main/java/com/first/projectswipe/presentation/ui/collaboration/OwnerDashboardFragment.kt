package com.first.projectswipe.presentation.ui.collaboration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.projectswipe.R
import com.first.projectswipe.databinding.FragmentOwnerDashboardBinding
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.network.dto.CollabApplicationResponse
import com.first.projectswipe.presentation.adapters.OwnerPostAdapter
import com.first.projectswipe.presentation.adapters.OwnerPostWithApplicants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OwnerDashboardFragment : Fragment() {

    private var _binding: FragmentOwnerDashboardBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var apiService: ApiService

    private lateinit var adapter: OwnerPostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOwnerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = OwnerPostAdapter(
            onAccept = { application -> acceptApplicant(application) },
            onDecline = { application -> declineApplicant(application) },
            onProfileClick = { userId -> navigateToProfile(userId) }
        )

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postsRecyclerView.adapter = adapter

        loadDashboard()
    }

    private fun loadDashboard() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val postsResponse = apiService.getCurrentUserCollaborations()
                if (postsResponse.isSuccessful) {
                    val posts = postsResponse.body() ?: emptyList()
                    if (posts.isEmpty()) {
                        showEmpty()
                        return@launch
                    }

                    val postsWithApplicants = posts.map { post ->
                        try {
                            val applicantsResponse = apiService.getCollabApplicants(post.id.toString())
                            val applicants = if (applicantsResponse.isSuccessful) {
                                applicantsResponse.body() ?: emptyList()
                            } else emptyList()
                            OwnerPostWithApplicants(post, applicants)
                        } catch (e: Exception) {
                            Log.e("OwnerDashboard", "Error loading applicants for ${post.id}", e)
                            OwnerPostWithApplicants(post)
                        }
                    }

                    adapter.submitList(postsWithApplicants)
                    binding.postsRecyclerView.visibility = View.VISIBLE
                    binding.emptyStateLayout.visibility = View.GONE
                } else {
                    showEmpty()
                }
            } catch (e: Exception) {
                Log.e("OwnerDashboard", "Error loading dashboard", e)
                Toast.makeText(context, "Error loading dashboard", Toast.LENGTH_SHORT).show()
                showEmpty()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun acceptApplicant(application: CollabApplicationResponse) {
        lifecycleScope.launch {
            try {
                val response = apiService.acceptApplicant(
                    application.collabPostId.toString(),
                    application.applicant.id
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "Accepted ${application.applicant.fullName}!", Toast.LENGTH_SHORT).show()
                    loadDashboard()
                } else {
                    Toast.makeText(context, "Failed to accept", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("OwnerDashboard", "Error accepting", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun declineApplicant(application: CollabApplicationResponse) {
        lifecycleScope.launch {
            try {
                val response = apiService.declineApplicant(
                    application.collabPostId.toString(),
                    application.applicant.id
                )
                if (response.isSuccessful) {
                    Toast.makeText(context, "Declined", Toast.LENGTH_SHORT).show()
                    loadDashboard()
                } else {
                    Toast.makeText(context, "Failed to decline", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("OwnerDashboard", "Error declining", e)
            }
        }
    }

    private fun navigateToProfile(userId: String) {
        val bundle = Bundle().apply { putString("viewedUserId", userId) }
        findNavController().navigate(R.id.action_ownerDashboardFragment_to_profileFragment, bundle)
    }

    private fun showEmpty() {
        binding.postsRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
