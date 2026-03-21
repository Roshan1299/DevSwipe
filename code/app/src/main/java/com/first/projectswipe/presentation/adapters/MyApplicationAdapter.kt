package com.first.projectswipe.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.first.projectswipe.R
import com.first.projectswipe.network.dto.CollabApplicationResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyApplicationAdapter : ListAdapter<CollabApplicationResponse, MyApplicationAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_application, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val projectTitle: TextView = itemView.findViewById(R.id.projectTitleTextView)
        private val appliedDate: TextView = itemView.findViewById(R.id.appliedDateTextView)
        private val status: TextView = itemView.findViewById(R.id.statusTextView)

        fun bind(application: CollabApplicationResponse) {
            projectTitle.text = application.collabPostTitle
            appliedDate.text = "Applied ${dateFormat.format(Date(application.createdAt * 1000))}"

            status.text = application.status.replaceFirstChar { it.uppercase() }
            when (application.status) {
                "PENDING" -> {
                    status.setTextColor(Color.parseColor("#FF9800"))
                    status.setBackgroundColor(Color.parseColor("#FFF3E0"))
                }
                "ACCEPTED" -> {
                    status.setTextColor(Color.parseColor("#4CAF50"))
                    status.setBackgroundColor(Color.parseColor("#E8F5E9"))
                }
                "DECLINED" -> {
                    status.setTextColor(Color.parseColor("#F44336"))
                    status.setBackgroundColor(Color.parseColor("#FFEBEE"))
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CollabApplicationResponse>() {
        override fun areItemsTheSame(a: CollabApplicationResponse, b: CollabApplicationResponse) = a.id == b.id
        override fun areContentsTheSame(a: CollabApplicationResponse, b: CollabApplicationResponse) = a == b
    }
}
