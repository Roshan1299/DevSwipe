package com.first.projectswipe.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.first.projectswipe.R
import com.first.projectswipe.network.dto.CollabApplicationResponse

class ApplicantAdapter(
    private val onAccept: (CollabApplicationResponse) -> Unit,
    private val onDecline: (CollabApplicationResponse) -> Unit,
    private val onProfileClick: (String) -> Unit = {}
) : ListAdapter<CollabApplicationResponse, ApplicantAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_applicant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImageView)
        private val name: TextView = itemView.findViewById(R.id.nameTextView)
        private val university: TextView = itemView.findViewById(R.id.universityTextView)
        private val skills: TextView = itemView.findViewById(R.id.skillsTextView)
        private val acceptBtn: ImageButton = itemView.findViewById(R.id.acceptButton)
        private val declineBtn: ImageButton = itemView.findViewById(R.id.declineButton)

        fun bind(application: CollabApplicationResponse) {
            val applicant = application.applicant
            name.text = applicant.fullName
            university.text = applicant.university ?: "Unknown university"

            val navigateToProfile = View.OnClickListener { onProfileClick(applicant.id) }
            profileImage.setOnClickListener(navigateToProfile)
            name.setOnClickListener(navigateToProfile)
            skills.text = if (application.applicantSkills.isNotEmpty()) {
                application.applicantSkills.joinToString(", ")
            } else {
                "No skills listed"
            }

            if (!applicant.profileImageUrl.isNullOrEmpty()) {
                Glide.with(profileImage.context)
                    .load(applicant.profileImageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(profileImage)
            }

            when (application.status) {
                "PENDING" -> {
                    acceptBtn.visibility = View.VISIBLE
                    declineBtn.visibility = View.VISIBLE
                    acceptBtn.setOnClickListener { onAccept(application) }
                    declineBtn.setOnClickListener { onDecline(application) }
                }
                "ACCEPTED" -> {
                    acceptBtn.visibility = View.GONE
                    declineBtn.visibility = View.GONE
                    name.text = "${applicant.fullName} ✓"
                }
                "DECLINED" -> {
                    acceptBtn.visibility = View.GONE
                    declineBtn.visibility = View.GONE
                    name.alpha = 0.5f
                    university.alpha = 0.5f
                    skills.alpha = 0.5f
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CollabApplicationResponse>() {
        override fun areItemsTheSame(a: CollabApplicationResponse, b: CollabApplicationResponse) = a.id == b.id
        override fun areContentsTheSame(a: CollabApplicationResponse, b: CollabApplicationResponse) = a == b
    }
}
