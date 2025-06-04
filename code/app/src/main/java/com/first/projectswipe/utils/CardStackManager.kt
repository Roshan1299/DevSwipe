package com.first.projectswipe.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea

class CardStackManager(
    private val context: Context,
    private val container: FrameLayout,
    private val allIdeas: List<ProjectIdea>,
    private val onCardSwiped: (ProjectIdea, Int) -> Unit
) {
    private val maxVisible = 3
    private val inflater = LayoutInflater.from(context)

    private var currentTopIndex = 0

    fun showInitialCards() {
        container.removeAllViews()
        currentTopIndex = 0

        // Add up to 3 cards in order
        for (i in 0 until maxVisible) {
            addCardAt(i)
        }
    }

    private fun addCardAt(position: Int) {
        val dataIndex = currentTopIndex + position
        if (dataIndex >= allIdeas.size) return

        val idea = allIdeas[dataIndex]
        val card = inflater.inflate(R.layout.card_flip_container, container, false)
        ProjectCardBinder.bind(card, context, idea)

        // Set position (offset and scale)
        val offset = 24 * position
        val scale = 1f - 0.03f * position
        card.translationY = offset.toFloat()
        card.scaleX = scale
        card.scaleY = scale
        card.elevation = (maxVisible - position).toFloat()

        card.post {
            card.x = (container.width - card.width) / 2f
        }

        // Attach swipe logic to top card
        if (position == 0) {
            setupSwipe(card, idea)
        }

        container.addView(card, 0)
    }

    private fun setupSwipe(card: View, idea: ProjectIdea) {
        SwipeHandler(card, 250f, 1000f) { direction ->
            container.removeView(card)
            currentTopIndex++

            // Add next card if available
            addCardAt(maxVisible - 1)

            restack()
            onCardSwiped(idea, direction)
        }.attach()
    }

    private fun restack() {
        for (i in 0 until container.childCount) {
            val card = container.getChildAt(container.childCount - 1 - i)
            val offset = 24 * i
            val scale = 1f - 0.03f * i

            card.animate()
                .translationY(offset.toFloat())
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(200)
                .start()

            card.elevation = (maxVisible - i).toFloat()

            val ideaIndex = currentTopIndex + i
            if (i == 0 && ideaIndex < allIdeas.size) {
                setupSwipe(card, allIdeas[ideaIndex])
            }
        }
    }
}
