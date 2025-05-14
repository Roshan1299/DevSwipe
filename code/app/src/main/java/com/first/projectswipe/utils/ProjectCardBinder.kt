package com.first.projectswipe.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.TextView
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

object ProjectCardBinder {

    fun bind(card: View, context: Context, idea: ProjectIdea) {
        // Front card views
        val titleView = card.findViewById<TextView>(R.id.projectTitleTextView)
        val descView = card.findViewById<TextView>(R.id.projectDescriptionTextView)
        val creatorView = card.findViewById<TextView>(R.id.projectCreatorName)
        val chipGroup = card.findViewById<ChipGroup>(R.id.projectTagsChipGroup)

        // Back card views
        val fullDetailsView = card.findViewById<TextView?>(R.id.fullDescriptionTextView)
        val githubView = card.findViewById<TextView?>(R.id.githubLink)
        val timelineView = card.findViewById<TextView?>(R.id.timeline)

        // Bind front
        titleView.text = idea.title
        descView.text = idea.description
        creatorView.text = idea.createdBy

        chipGroup.removeAllViews()
        idea.tags?.forEach { tag ->
            val chip = Chip(context).apply {
                text = tag
                isCheckable = false
                isClickable = false
            }
            chipGroup.addView(chip)
        }

        // Bind back (optional — add checks if null)
        fullDetailsView?.text = idea.description
        githubView?.text = "GitHub: https://github.com/yourproject"
        timelineView?.text = "Timeline: 4–6 weeks"

        // Flip logic
        val front = card.findViewById<View>(R.id.frontCardLayout)
        val back = card.findViewById<View>(R.id.backCardLayout)
        val infoButton = front.findViewById<View>(R.id.infoButton)

        var isFlipped = false
        infoButton.setOnClickListener {
            isFlipped = !isFlipped
            flipCard(card, front, back, isFlipped)
        }
    }

    private fun flipCard(root: View, front: View, back: View, showBack: Boolean) {
        val scale = root.context.resources.displayMetrics.density
        root.cameraDistance = 8000 * scale

        val outAnim = ObjectAnimator.ofFloat(root, "rotationY", 0f, 90f).apply {
            duration = 200
        }

        val inAnim = ObjectAnimator.ofFloat(root, "rotationY", -90f, 0f).apply {
            duration = 200
        }

        outAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                front.visibility = if (showBack) View.GONE else View.VISIBLE
                back.visibility = if (showBack) View.VISIBLE else View.GONE
                inAnim.start()
            }
        })

        outAnim.start()
    }
}
