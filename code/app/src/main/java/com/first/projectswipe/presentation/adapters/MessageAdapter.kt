package com.first.projectswipe.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.databinding.ItemMessageBinding
import com.first.projectswipe.network.dto.MessageResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val currentUserId: String? = null
) : ListAdapter<MessageResponse, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

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
            val isSentByCurrentUser = currentUserId != null && message.sender.id == currentUserId
            val timeString = timeFormat.format(Date(message.timestamp))

            if (isSentByCurrentUser) {
                binding.sentMessageContainer.visibility = View.VISIBLE
                binding.receivedMessageContainer.visibility = View.GONE
                binding.sentMessageTextView.text = message.content
                binding.sentTimestampTextView.text = timeString
            } else {
                binding.receivedMessageContainer.visibility = View.VISIBLE
                binding.sentMessageContainer.visibility = View.GONE
                binding.receivedMessageTextView.text = message.content
                binding.receivedTimestampTextView.text = timeString
            }
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
