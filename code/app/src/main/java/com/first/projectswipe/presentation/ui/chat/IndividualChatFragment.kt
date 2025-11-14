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
import com.first.projectswipe.databinding.FragmentIndividualChatBinding
import com.first.projectswipe.presentation.adapters.MessageAdapter
import com.first.projectswipe.presentation.ui.auth.AuthManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IndividualChatFragment : Fragment() {

    private var _binding: FragmentIndividualChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager

    private lateinit var messageAdapter: MessageAdapter

    // Store the other user's ID (passed from conversations list)
    private var otherUserId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndividualChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Extract other user ID from arguments
        otherUserId = arguments?.getString("conversationId") ?: ""

        // Set up the toolbar with user info
        setupToolbar()

        // Setup recycler view for messages
        setupRecyclerView()

        // Observe view model
        observeViewModel()

        // Load messages for this conversation
        loadMessages()

        // Set up send button
        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        // Send message when pressing Enter in the EditText
        binding.messageEditText.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun setupToolbar() {
        // For now, we'll set placeholder values
        // In a real implementation, we'd get this from the user details
        // and potentially make an API call to get user info based on otherUserId
    }

    private fun setupRecyclerView() {
        val currentUserId = authManager.getCurrentUserId()
        messageAdapter = MessageAdapter(currentUserId)
        binding.messagesRecyclerView.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages)
            // Scroll to bottom when new messages arrive
            binding.messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            // Handle UI state changes if needed
        }
    }

    private fun loadMessages() {
        // Load messages for the specific conversation
        // The conversation ID is passed as an argument
        viewModel.getConversationMessages(args.conversationId)
    }

    private fun sendMessage() {
        val messageText = binding.messageEditText.text.toString().trim()
        if (messageText.isNotEmpty() && otherUserId.isNotEmpty()) {
            // Get the current user's ID from auth manager
            val currentUserId = authManager.getCurrentUserId()
            
            if (currentUserId != null) {
                viewModel.sendMessage(otherUserId, messageText)
                binding.messageEditText.setText("")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}