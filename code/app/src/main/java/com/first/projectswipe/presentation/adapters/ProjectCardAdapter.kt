package com.first.projectswipe.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.data.models.ProjectIdea

class ProjectCardAdapter(private val projectList: List<ProjectIdea>) :
    RecyclerView.Adapter<ProjectCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.projectTitleTextView)
        val descView: TextView = view.findViewById(R.id.projectDescriptionTextView)
        val creatorView: TextView = view.findViewById(R.id.projectCreatorName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_card_front, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projectList[position]
        holder.titleView.text = project.title
        holder.descView.text = project.previewDescription
        holder.creatorView.text = project.createdBy.fullName
    }

    override fun getItemCount(): Int = projectList.size
}
