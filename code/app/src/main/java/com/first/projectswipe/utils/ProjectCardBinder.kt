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
        // Front side
        val frontLayout = card.findViewById<View>(R.id.frontCardLayout)
        val frontTitle = frontLayout.findViewById<TextView>(R.id.projectTitleTextView)
        val frontDesc = frontLayout.findViewById<TextView>(R.id.projectDescriptionTextView)
        val frontCreator = frontLayout.findViewById<TextView>(R.id.projectCreatorName)
        val frontChips = frontLayout.findViewById<ChipGroup>(R.id.projectTagsChipGroup)

        frontTitle.text = idea.title
        frontDesc.text = idea.description
        frontCreator.text = idea.createdBy
        frontChips.removeAllViews()
        idea.tags.forEach { tag ->
            val chip = Chip(context).apply {
                text = tag
                isCheckable = false
                isClickable = false
            }
            frontChips.addView(chip)
        }

        // Back side
        val backLayout = card.findViewById<View>(R.id.backCardLayout)
        val backDesc = backLayout.findViewById<TextView>(R.id.fullDescriptionTextView)
        val backGithub = backLayout.findViewById<TextView>(R.id.githubLink)
        val backTimeline = backLayout.findViewById<TextView>(R.id.timeline)
        val backChips = backLayout.findViewById<ChipGroup>(R.id.projectTagsChipGroup)

        backDesc.text = idea.description
        backGithub.text = "GitHub: https://github.com/yourproject"
        backTimeline.text = "Timeline: 4–6 weeks"
        backChips.removeAllViews()
        idea.tags.forEach { tag ->
            val chip = Chip(context).apply {
                text = tag
                isCheckable = false
                isClickable = false
            }
            backChips.addView(chip)
        }

        // Flip logic
        val frontInfo = frontLayout.findViewById<View>(R.id.infoButton)
        val backInfo = backLayout.findViewById<View>(R.id.infoButton)

        frontLayout.visibility = View.VISIBLE
        backLayout.visibility = View.GONE

        var isFlipped = false
        val flip = {
            isFlipped = !isFlipped
            flipCard(card, frontLayout, backLayout, isFlipped)
        }

        frontInfo.setOnClickListener { flip() }
        backInfo.setOnClickListener { flip() }

        // ✅ Like/Dislike swipe buttons
        val likeButton = frontLayout.findViewById<View>(R.id.likeButton)
        val dislikeButton = frontLayout.findViewById<View>(R.id.dislikeButton)

        likeButton.setOnClickListener {
            (card.getTag(R.id.swipe_handler_tag) as? SwipeHandler)?.swipeRight()
        }

        dislikeButton.setOnClickListener {
            (card.getTag(R.id.swipe_handler_tag) as? SwipeHandler)?.swipeLeft()
        }
    }

    private fun flipCard(root: View, front: View, back: View, showBack: Boolean) {
        val scale = root.context.resources.displayMetrics.density
        root.cameraDistance = 8000 * scale

        val outAnim = ObjectAnimator.ofFloat(root, "rotationY", 0f, 90f).apply { duration = 200 }
        val inAnim = ObjectAnimator.ofFloat(root, "rotationY", -90f, 0f).apply { duration = 200 }

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
