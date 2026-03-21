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
import com.first.projectswipe.databinding.FragmentMyApplicationsBinding
import com.first.projectswipe.network.ApiService
import com.first.projectswipe.presentation.adapters.MyApplicationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyApplicationsFragment : Fragment() {

    private var _binding: FragmentMyApplicationsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var apiService: ApiService

    private lateinit var adapter: MyApplicationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = MyApplicationAdapter()
        binding.applicationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.applicationsRecyclerView.adapter = adapter

        loadApplications()
    }

    private fun loadApplications() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = apiService.getMyApplications()
                if (response.isSuccessful) {
                    val applications = response.body() ?: emptyList()
                    if (applications.isEmpty()) {
                        showEmpty()
                    } else {
                        adapter.submitList(applications)
                        binding.applicationsRecyclerView.visibility = View.VISIBLE
                        binding.emptyStateLayout.visibility = View.GONE
                    }
                } else {
                    showEmpty()
                }
            } catch (e: Exception) {
                Log.e("MyApplications", "Error loading applications", e)
                Toast.makeText(context, "Error loading applications", Toast.LENGTH_SHORT).show()
                showEmpty()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showEmpty() {
        binding.applicationsRecyclerView.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
