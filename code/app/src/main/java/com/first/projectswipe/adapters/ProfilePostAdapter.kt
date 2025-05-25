package com.first.projectswipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ProfilePostAdapter(private val projects: List<ProjectIdea>) :
    RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.projectTitleTextView)
        val tagsChipGroup: ChipGroup = view.findViewById(R.id.skillsChipGroup)
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
//                setChipBackgroundColorResource(R.color.chip_background)
                setTextColor(context.getColor(android.R.color.black))
            }
            holder.tagsChipGroup.addView(chip)
        }
    }

    override fun getItemCount(): Int = projects.size
}
