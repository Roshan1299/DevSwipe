package com.first.projectswipe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePostAdapter(private val projects: MutableList<ProjectIdea>) :
    RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>() {

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
                        deleteProject(project, position)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = projects.size

    private fun deleteProject(project: ProjectIdea, position: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("project_ideas").document(project.id)
            .delete()
            .addOnSuccessListener {
                projects.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, projects.size)
            }
            .addOnFailureListener {
                Toast.makeText(
                    null, "Failed to delete project: ${it.message}", Toast.LENGTH_SHORT
                ).show()
            }
    }
}
