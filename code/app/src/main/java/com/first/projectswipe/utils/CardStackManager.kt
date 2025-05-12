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
    private val allIdeas: MutableList<ProjectIdea>,
    private val onCardSwiped: (ProjectIdea, Int) -> Unit
) {
    private val currentCards = mutableListOf<View>()
    private val maxVisible = 3
    private val inflater = LayoutInflater.from(context)

    fun showInitialCards() {
        container.removeAllViews()
        currentCards.clear()

        val visible = allIdeas.take(maxVisible)
        visible.reversed().forEachIndexed { index, idea ->
            addCard(idea, index, animate = true)
        }
    }

    private fun addCard(idea: ProjectIdea, position: Int, animate: Boolean = false) {
        val card = inflater.inflate(R.layout.item_project_card, container, false)
        ProjectCardBinder.bind(card, context, idea)

        val offset = 24 * position
        val scale = 1f - 0.03f * position

        card.translationY = offset.toFloat()
        card.scaleX = scale
        card.scaleY = scale
        card.elevation = (maxVisible - position).toFloat()

        card.post {
            card.x = (container.width - card.width) / 2f
        }

        if (position == 0) {
            SwipeHandler(card, 250f, 1000f) { direction ->
                container.removeView(card)
                currentCards.remove(card)
                allIdeas.remove(idea)
                restack()

                // Add next card if any
                val nextIndex = currentCards.size
                if (nextIndex < allIdeas.size) {
                    addCard(allIdeas[nextIndex], maxVisible - 1)
                }

                onCardSwiped(idea, direction)
            }.attach()
        }

        container.addView(card, 0)
        currentCards.add(0, card)

        if (animate) {
            card.translationY += 100f
            card.alpha = 0f
            card.animate()
                .translationY(offset.toFloat())
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }

    private fun restack() {
        currentCards.forEachIndexed { index, card ->
            card.animate()
                .translationY((24 * index).toFloat())
                .scaleX(1f - 0.03f * index)
                .scaleY(1f - 0.03f * index)
                .setDuration(200)
                .start()

            card.elevation = (maxVisible - index).toFloat()
            card.setOnTouchListener(null)

            val idea = allIdeas.getOrNull(index)
            if (index == 0 && idea != null) {
                SwipeHandler(card, 250f, 1000f) { direction ->
                    container.removeView(card)
                    currentCards.remove(card)
                    allIdeas.remove(idea)
                    restack()

                    if (currentCards.size < maxVisible && allIdeas.size > currentCards.size) {
                        addCard(allIdeas[currentCards.size], maxVisible - 1)
                    }

                    onCardSwiped(idea, direction)
                }.attach()
            }
        }
    }
}
