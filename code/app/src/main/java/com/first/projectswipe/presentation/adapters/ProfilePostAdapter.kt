package com.first.projectswipe.presentation.adapters

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
        holder.titleView.text = project.title

        holder.tagsChipGroup.removeAllViews()
        val context = holder.itemView.context
        project.tags.forEach { tag ->
            val chip = Chip(context).apply {
                text = tag
                isClickable = false
                isCheckable = false
                setTextColor(context.getColor(android.R.color.black))
            }
            holder.tagsChipGroup.addView(chip)
        }

        holder.optionsButton.setOnClickListener { v ->
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(R.menu.menu_profile_project, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete_project -> {
                        deleteProject(project, position, v)
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

    private fun deleteProject(project: ProjectIdea, position: Int, view: View) {
        // Call the callback to handle deletion in the fragment
        onProjectDeleted(project, position)
    }

    private fun editProject(view: View, project: ProjectIdea) {
        val bundle = Bundle().apply {
            putString("projectId", project.id.toString())
        }
        view.findNavController().navigate(R.id.createProjectIdeaFragment, bundle)
    }
}
