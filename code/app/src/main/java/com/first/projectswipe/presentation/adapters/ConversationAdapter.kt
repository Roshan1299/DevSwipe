package com.first.projectswipe.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.first.projectswipe.data.models.Conversation
import com.first.projectswipe.databinding.ItemConversationBinding
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter(
    private val onItemClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationAdapter.ConversationViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConversationViewHolder(
        private val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.apply {
                // Set user name
                userNameTextView.text = conversation.otherUser.fullName

                // Set last message preview
                lastMessageTextView.text = conversation.lastMessage ?: "No messages yet"

                // Format and set timestamp
                if (conversation.lastMessageTime != null) {
                    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                    val date = Date(conversation.lastMessageTime)
                    timestampTextView.text = dateFormat.format(date)
                } else {
                    timestampTextView.text = ""
                }

                // Handle unread count
                if (conversation.unreadCount > 0) {
                    unreadCountTextView.visibility = android.view.View.VISIBLE
                    unreadCountTextView.text = conversation.unreadCount.toString()
                } else {
                    unreadCountTextView.visibility = android.view.View.GONE
                }

                // Load profile image
                val profileImageUrl = conversation.otherUser.profileImageUrl
                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide.with(profileImageView.context)
                        .load(profileImageUrl)
                        .circleCrop()
                        .placeholder(com.first.projectswipe.R.drawable.ic_profile_placeholder)
                        .into(profileImageView)
                } else {
                    // Set default profile image
                    Glide.with(profileImageView.context)
                        .load(com.first.projectswipe.R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(profileImageView)
                }

                // Set click listener for the item
                root.setOnClickListener {
                    onItemClick(conversation)
                }
            }
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}