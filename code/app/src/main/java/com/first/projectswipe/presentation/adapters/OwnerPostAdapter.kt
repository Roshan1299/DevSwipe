package com.first.projectswipe.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.network.dto.CollabApplicationResponse
import com.first.projectswipe.network.dto.CollaborationResponse

data class OwnerPostWithApplicants(
    val post: CollaborationResponse,
    val applicants: List<CollabApplicationResponse> = emptyList(),
    val isExpanded: Boolean = false
)

class OwnerPostAdapter(
    private val onAccept: (CollabApplicationResponse) -> Unit,
    private val onDecline: (CollabApplicationResponse) -> Unit,
    private val onProfileClick: (String) -> Unit = {}
) : ListAdapter<OwnerPostWithApplicants, OwnerPostAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_owner_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.postTitleTextView)
        private val statusBadge: TextView = itemView.findViewById(R.id.statusBadge)
        private val teamSize: TextView = itemView.findViewById(R.id.teamSizeTextView)
        private val applicantCount: TextView = itemView.findViewById(R.id.applicantCountTextView)
        private val applicantsRv: RecyclerView = itemView.findViewById(R.id.applicantsRecyclerView)

        fun bind(item: OwnerPostWithApplicants) {
            val post = item.post
            title.text = post.projectTitle
            statusBadge.text = post.status.replaceFirstChar { it.uppercase() }
            teamSize.text = "Team: ${post.currentTeamSize}/${post.teamSize} members"

            val pendingCount = item.applicants.count { it.status == "PENDING" }
            applicantCount.text = if (pendingCount > 0) {
                "$pendingCount pending applicant${if (pendingCount > 1) "s" else ""}"
            } else if (item.applicants.isEmpty()) {
                "No applicants yet"
            } else {
                "${item.applicants.size} applicant${if (item.applicants.size > 1) "s" else ""}"
            }

            // Toggle applicants list on click
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val current = getItem(pos)
                    val updated = currentList.toMutableList()
                    updated[pos] = current.copy(isExpanded = !current.isExpanded)
                    submitList(updated)
                }
            }

            if (item.isExpanded && item.applicants.isNotEmpty()) {
                applicantsRv.visibility = View.VISIBLE
                val adapter = ApplicantAdapter(onAccept, onDecline, onProfileClick)
                applicantsRv.layoutManager = LinearLayoutManager(itemView.context)
                applicantsRv.adapter = adapter
                adapter.submitList(item.applicants)
            } else {
                applicantsRv.visibility = View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OwnerPostWithApplicants>() {
        override fun areItemsTheSame(a: OwnerPostWithApplicants, b: OwnerPostWithApplicants) = a.post.id == b.post.id
        override fun areContentsTheSame(a: OwnerPostWithApplicants, b: OwnerPostWithApplicants) = a == b
    }
}
