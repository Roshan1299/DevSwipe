package com.first.projectswipe.presentation.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.navArgs
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

    private val args: IndividualChatFragmentArgs by navArgs()

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
        otherUserId = args.otherUserId ?: ""

        // Setup recycler view for messages
        setupRecyclerView()

        // Observe view model
        observeViewModel()

        // Load messages for this conversation first to get other user info
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

    private fun setupRecyclerView() {
        val currentUserId = authManager.getCurrentUserId()
        messageAdapter = MessageAdapter(currentUserId)
        binding.messagesRecyclerView.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                messageAdapter.submitList(messages)
                // Scroll to bottom when new messages arrive
                binding.messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)

                // Update toolbar with the other user's info if we have messages
                if (messages.isNotEmpty()) {
                    // Get the other user's info from the first message
                    val firstMessage = messages.first()
                    val otherUser = if (firstMessage.sender.id == authManager.getCurrentUserId()) {
                        firstMessage.receiver
                    } else {
                        firstMessage.sender
                    }
                    updateToolbarWithUser(otherUser)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                // Handle UI state changes if needed
            }
        }
    }

    private fun updateToolbarWithUser(user: com.first.projectswipe.network.dto.UserDto) {
        binding.userNameTextView.text = user.fullName
        binding.userStatusTextView.text = "Online"  // Could be dynamic in the future

        // Load profile image
        val profileImageUrl = user.profileImageUrl
        if (!profileImageUrl.isNullOrEmpty()) {
            com.bumptech.glide.Glide.with(binding.profileImageView.context)
                .load(profileImageUrl)
                .circleCrop()
                .placeholder(com.first.projectswipe.R.drawable.ic_profile_placeholder)
                .into(binding.profileImageView)
        } else {
            com.bumptech.glide.Glide.with(binding.profileImageView.context)
                .load(com.first.projectswipe.R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(binding.profileImageView)
        }
    }

    private fun loadMessages() {
        // Load messages for the specific conversation
        // The other user ID is passed as an argument
        viewModel.getConversationMessages(otherUserId)
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
        // Mark messages as read when leaving the chat
        if (otherUserId.isNotEmpty()) {
            viewModel.markMessagesAsRead(otherUserId)
        }
        super.onDestroyView()
        _binding = null
    }
}