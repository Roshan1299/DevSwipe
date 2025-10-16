package com.first.projectswipe.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.first.projectswipe.R
import com.first.projectswipe.data.models.CollabPost
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.first.projectswipe.network.ApiService

object CollabCardBinder {

    fun bind(card: View, context: Context, collabPost: CollabPost, apiService: ApiService) {
        val swipeHandler = card.getTag(R.id.swipe_handler_tag) as? SwipeHandler

        // --- FRONT SIDE ---
        val frontLayout = card.findViewById<View>(R.id.frontCardLayout)
        val frontTitle = frontLayout.findViewById<TextView>(R.id.projectTitleTextView)
        val frontDesc = frontLayout.findViewById<TextView>(R.id.projectDescriptionTextView)
        val frontCreator = frontLayout.findViewById<TextView>(R.id.projectCreatorName)
        val frontPfp = frontLayout.findViewById<ImageView>(R.id.projectCreatorAvatar)
        val frontChips = frontLayout.findViewById<ChipGroup>(R.id.projectTagsChipGroup)
        val likeButtonFront = frontLayout.findViewById<View>(R.id.likeButton)
        val dislikeButtonFront = frontLayout.findViewById<View>(R.id.dislikeButton)

        frontTitle.text = collabPost.projectTitle
        frontDesc.text = collabPost.description
        frontChips.removeAllViews()
        collabPost.skillsNeeded.forEach { skill ->
            val chip = Chip(context).apply {
                text = skill
                isCheckable = false
                isClickable = false
            }
            frontChips.addView(chip)
        }

        UserUtils.getUserInfo(collabPost.createdBy.id, apiService) { userInfo ->
            frontCreator.text = userInfo.name
            userInfo.profileImageUrl?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(frontPfp)
            } ?: frontPfp.setImageResource(R.drawable.ic_profile_placeholder)
        }

        // --- BACK SIDE ---
        val backLayout = card.findViewById<View>(R.id.backCardLayout)
        val backDesc = backLayout.findViewById<TextView>(R.id.fullDescriptionTextView)
        val backChips = backLayout.findViewById<ChipGroup>(R.id.projectTagsChipGroup)
        val likeButtonBack = backLayout.findViewById<View>(R.id.likeButtonBack)
        val dislikeButtonBack = backLayout.findViewById<View>(R.id.dislikeButtonBack)

        // Set the detailed info on back card
        backDesc.text = collabPost.description
        backChips.removeAllViews()
        collabPost.skillsNeeded.forEach { skill ->
            val chip = Chip(context).apply {
                text = skill
                isCheckable = false
                isClickable = false
            }
            backChips.addView(chip)
        }

        // Add team size and time commitment info to back
        backLayout.findViewById<TextView>(R.id.difficulty).apply {
            text = "Time Commitment: ${collabPost.timeCommitment} | Team Size: ${collabPost.currentTeamSize}/${collabPost.teamSize}"
        }
        
        // Hide the GitHub link field and use it for status information instead
        val githubLink = backLayout.findViewById<TextView>(R.id.githubLink)
        githubLink.text = "Status: ${collabPost.status}"

        // --- FLIP LOGIC ---
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

        // --- SWIPE ACTIONS ---
        likeButtonFront?.setOnClickListener { swipeHandler?.swipeRight() }
        dislikeButtonFront?.setOnClickListener { swipeHandler?.swipeLeft() }
        likeButtonBack?.setOnClickListener { swipeHandler?.swipeRight() }
        dislikeButtonBack?.setOnClickListener { swipeHandler?.swipeLeft() }
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
