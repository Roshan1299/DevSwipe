package com.first.projectswipe.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.data.models.CollabPost

class CollabPostAdapter(private val collabPostList: List<CollabPost>) :
    RecyclerView.Adapter<CollabPostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.projectTitleTextView) // Reusing the same IDs as ProjectCard
        val descriptionView: TextView = view.findViewById(R.id.projectDescriptionTextView)
        val creatorView: TextView = view.findViewById(R.id.projectCreatorName)
//        val skillsView: TextView = view.findViewById(R.id.skillsNeededTextView) // Additional view for skills
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_card_front, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collabPost = collabPostList[position]
        holder.titleView.text = collabPost.projectTitle
        holder.descriptionView.text = collabPost.description
        holder.creatorView.text = collabPost.createdBy.fullName
//        // Format skills as a comma-separated string
//        holder.skillsView.text = if (collabPost.skillsNeeded.isNotEmpty()) {
//            "Skills needed: ${collabPost.skillsNeeded.joinToString(", ")}"
//        } else {
//            "Skills needed: None specified"
//        }
    }

    override fun getItemCount(): Int = collabPostList.size
}
