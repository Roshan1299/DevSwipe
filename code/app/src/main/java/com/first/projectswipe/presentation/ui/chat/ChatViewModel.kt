package com.first.projectswipe.presentation.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.projectswipe.data.models.Conversation
import com.first.projectswipe.domain.repository.ChatRepository
import com.first.projectswipe.network.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations
    
    private val _messages = MutableStateFlow<List<MessageResponse>>(emptyList())
    val messages: StateFlow<List<MessageResponse>> = _messages
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState
    
    private val _sendMessageResult = MutableStateFlow<SendMessageResponse?>(null)
    val sendMessageResult: StateFlow<SendMessageResponse?> = _sendMessageResult
    
    fun getConversations() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            chatRepository.getConversations()
                .onSuccess { conversationResponses ->
                    val conversationModels = conversationResponses.map { response ->
                        Conversation(
                            id = response.id,
                            otherUser = response.otherUser,
                            lastMessage = response.lastMessage,
                            lastMessageTime = response.lastMessageTime,
                            unreadCount = response.unreadCount
                        )
                    }
                    _conversations.value = conversationModels
                    _uiState.value = UiState.Success
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun getConversationMessages(otherUserId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            chatRepository.getConversationMessages(otherUserId, 0, 50) // Load 50 messages initially
                .onSuccess { messages ->
                    _messages.value = messages
                    _uiState.value = UiState.Success
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun sendMessage(receiverId: String, content: String, messageType: String = "TEXT") {
        viewModelScope.launch {
            val request = MessageRequest(receiverId, content, messageType)
            chatRepository.sendMessage(request)
                .onSuccess { response ->
                    _sendMessageResult.value = response
                    if (response.success && response.message != null) {
                        // Add the new message to the current messages list
                        val updatedMessages = _messages.value + response.message
                        _messages.value = updatedMessages
                    }
                    _uiState.value = UiState.Success
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun markMessagesAsRead(otherUserId: String) {
        viewModelScope.launch {
            chatRepository.markMessagesAsRead(otherUserId)
                .onSuccess {
                    // Update unread count in conversations list
                    // This would require updating the specific conversation
                }
                .onFailure { 
                    // Handle error silently or show notification
                }
        }
    }
    
    fun getUnreadCount() {
        viewModelScope.launch {
            chatRepository.getUnreadCount()
                .onSuccess { count ->
                    // Update unread count in UI if needed
                }
                .onFailure {
                    // Handle error silently or show notification
                }
        }
    }
}

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String) : UiState()
}