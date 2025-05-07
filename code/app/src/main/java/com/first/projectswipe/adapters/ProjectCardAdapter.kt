package com.first.projectswipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ProjectCardAdapter(
    private val projects: MutableList<ProjectIdea>
) : RecyclerView.Adapter<ProjectCardAdapter.CardViewHolder>() {

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.projectTitleTextView)
        val descTextView: TextView = view.findViewById(R.id.projectDescriptionTextView)
        val chipGroup: ChipGroup = view.findViewById(R.id.projectTagsChipGroup)
        val likeButton: ImageButton = view.findViewById(R.id.likeButton)
        val dislikeButton: ImageButton = view.findViewById(R.id.dislikeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val project = projects[position]

        holder.titleTextView.text = project.title
        holder.descTextView.text = project.description

        // Setup tags
        holder.chipGroup.removeAllViews()
        project.tags?.forEach { tag ->
            val chip = Chip(holder.chipGroup.context).apply {
                text = tag
                isClickable = false
                isCheckable = false
            }
            holder.chipGroup.addView(chip)
        }

        // Like/Dislike button actions
        holder.likeButton.setOnClickListener {
            removeTopItem(position)
            // You can add logic to save liked item
        }

        holder.dislikeButton.setOnClickListener {
            removeTopItem(position)
            // You can add logic to record dislikes
        }
    }

    override fun getItemCount(): Int = projects.size

    fun removeTopItem(position: Int) {
        if (position < projects.size) {
            projects.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
