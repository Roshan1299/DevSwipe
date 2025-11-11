package com.first.projectswipe.presentation.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.first.projectswipe.databinding.FragmentChatBinding
import com.first.projectswipe.presentation.adapters.ConversationAdapter
import com.first.projectswipe.presentation.ui.auth.AuthManager
import dagger.hilt.android.AndroidEntryPoint
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

        // Set up send button click listener
        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        // Send message when pressing Enter in the EditText
        binding.messageEditText.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun setupRecyclerView() {
        conversationAdapter = ConversationAdapter { conversation ->
            // Navigate to individual chat screen
            // For now, passing the conversation ID - but normally you'd pass the other user ID
            findNavController().navigate(
                R.id.action_chatFragment_to_individualChatFragment,
                Bundle().apply {
                    putString("conversationId", conversation.id)
                }
            )
        }
        
        binding.recyclerView.apply {
            adapter = conversationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.conversations.observe(viewLifecycleOwner) { conversations ->
            conversationAdapter.submitList(conversations)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
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
                else -> {} // Do nothing for Idle state
            }
        }
    }

    private fun loadConversations() {
        viewModel.getConversations()
    }

    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()
        if (messageText.isNotEmpty()) {
            // For now, this would send a test message - in real implementation,
            // this would be handled in the individual chat screen
            binding.messageEditText.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}