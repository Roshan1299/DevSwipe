package com.first.projectswipe.presentation.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.projectswipe.databinding.FragmentChatBinding
import com.first.projectswipe.presentation.adapters.ConversationAdapter
import com.first.projectswipe.presentation.ui.auth.AuthManager
import dagger.hilt.android.AndroidEntryPoint
import com.first.projectswipe.presentation.ui.chat.ChatFragmentDirections
import javax.inject.Inject
@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager

    private lateinit var conversationAdapter: ConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        loadConversations()
    }

    private fun setupRecyclerView() {
        conversationAdapter = ConversationAdapter { conversation ->
            // Navigate to individual chat screen
            // Pass the other user's ID to the individual chat fragment
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToIndividualChatFragment(conversation.otherUser.id)
            )
        }

        binding.recyclerView.apply {
            adapter = conversationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.conversations.collect { conversations ->
                conversationAdapter.submitList(conversations)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        // Handle error (show snackbar, etc.)
                    }
                    is UiState.Idle -> {} // Do nothing for Idle state
                }
            }
        }
    }

    private fun loadConversations() {
        viewModel.getConversations()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}