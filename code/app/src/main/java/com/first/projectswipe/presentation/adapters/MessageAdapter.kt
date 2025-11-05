package com.first.projectswipe.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.databinding.ItemMessageBinding
import com.first.projectswipe.network.dto.MessageResponse

class MessageAdapter(
    private val currentUserId: String? = null
) : ListAdapter<MessageResponse, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(
        private val binding: ItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageResponse) {
            binding.apply {
                // Determine if message is sent by current user
                val isSentByCurrentUser = isMessageFromCurrentUser(message)

                if (isSentByCurrentUser) {
                    // Show sent message layout
                    sentMessageLayout.visibility = android.view.View.VISIBLE
                    receivedMessageLayout.visibility = android.view.View.GONE
                    sentMessageTextView.text = message.content
                } else {
                    // Show received message layout
                    receivedMessageLayout.visibility = android.view.View.VISIBLE
                    sentMessageLayout.visibility = android.view.View.GONE
                    receivedMessageTextView.text = message.content
                }
            }
        }

        private fun isMessageFromCurrentUser(message: MessageResponse): Boolean {
            return currentUserId != null && message.sender.id == currentUserId
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<MessageResponse>() {
        override fun areItemsTheSame(oldItem: MessageResponse, newItem: MessageResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageResponse, newItem: MessageResponse): Boolean {
            return oldItem == newItem
        }
    }
}