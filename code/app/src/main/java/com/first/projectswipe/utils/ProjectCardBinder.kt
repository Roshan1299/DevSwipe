package com.first.projectswipe.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

object ProjectCardBinder {
    fun bind(card: View, context: Context, idea: ProjectIdea) {
        card.findViewById<TextView>(R.id.projectTitleTextView).text = idea.title
        card.findViewById<TextView>(R.id.projectDescriptionTextView).text = idea.description

        val chipGroup = card.findViewById<ChipGroup>(R.id.projectTagsChipGroup)
        chipGroup.removeAllViews()
        idea.tags?.forEach { tag ->
            val chip = Chip(context).apply {
                text = tag
                isCheckable = false
                isClickable = false
            }
            chipGroup.addView(chip)
        }
    }
}
