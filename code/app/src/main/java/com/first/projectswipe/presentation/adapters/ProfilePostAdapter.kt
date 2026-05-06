package com.first.projectswipe.presentation.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.data.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class ProfilePostAdapter(
    private val projects: MutableList<ProjectIdea>,
    private val onProjectDeleted: (ProjectIdea, Int) -> Unit
) : RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.projectTitleTextView)
        val descriptionView: TextView = view.findViewById(R.id.projectDescriptionTextView)
        val difficultyBadge: TextView = view.findViewById(R.id.difficultyBadge)
        val tagsChipGroup: ChipGroup = view.findViewById(R.id.skillsChipGroup)
        val optionsButton: ImageButton = view.findViewById(R.id.moreOptionsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        val context = holder.itemView.context

        holder.titleView.text = project.title
        holder.descriptionView.text = project.previewDescription

        // Difficulty badge
        val difficulty = project.difficulty.uppercase()
        holder.difficultyBadge.text = difficulty
        when {
            difficulty.contains("EASY") || difficulty.contains("BEGINNER") -> {
                holder.difficultyBadge.setBackgroundResource(R.drawable.difficulty_badge_easy)
                holder.difficultyBadge.setTextColor(Color.parseColor("#2E7D32"))
            }
            difficulty.contains("MEDIUM") || difficulty.contains("INTERMEDIATE") -> {
                holder.difficultyBadge.setBackgroundResource(R.drawable.difficulty_badge_medium)
                holder.difficultyBadge.setTextColor(Color.parseColor("#E65100"))
            }
            difficulty.contains("HARD") || difficulty.contains("ADVANCED") -> {
                holder.difficultyBadge.setBackgroundResource(R.drawable.difficulty_badge_hard)
                holder.difficultyBadge.setTextColor(Color.parseColor("#C62828"))
            }
            else -> {
                holder.difficultyBadge.setBackgroundResource(R.drawable.difficulty_badge_easy)
                holder.difficultyBadge.setTextColor(Color.parseColor("#2E7D32"))
            }
        }

        // Tags as styled chips
        holder.tagsChipGroup.removeAllViews()
        project.tags.take(3).forEach { tag ->
            val chip = Chip(context).apply {
                text = tag
                isClickable = false
                isCheckable = false
                setTextColor(Color.parseColor("#007AFF"))
                chipBackgroundColor = ColorStateList.valueOf(Color.parseColor("#E8F2FF"))
                chipStrokeWidth = 0f
                setEnsureMinTouchTargetSize(false)
                textSize = 11f
            }
            holder.tagsChipGroup.addView(chip)
        }

        holder.optionsButton.setOnClickListener { v ->
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(R.menu.menu_profile_project, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete_project -> {
                        onProjectDeleted(project, position)
                        true
                    }
                    R.id.action_edit_project -> {
                        editProject(v, project)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = projects.size

    private fun editProject(view: View, project: ProjectIdea) {
        val bundle = Bundle().apply {
            putString("projectId", project.id.toString())
        }
        view.findNavController().navigate(R.id.createProjectIdeaFragment, bundle)
    }
}
