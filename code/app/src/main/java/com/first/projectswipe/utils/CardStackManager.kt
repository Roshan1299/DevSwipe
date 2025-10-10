package com.first.projectswipe.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import com.first.projectswipe.R
import com.first.projectswipe.data.models.ProjectIdea

import com.first.projectswipe.network.ApiService

class CardStackManager(
    private val context: Context,
    private val container: FrameLayout,
    var allIdeas: List<ProjectIdea>,
    private val apiService: ApiService,
    private val startingIndex: Int = 0,
    private val onCardSwiped: (ProjectIdea, Int) -> Unit
) {
    private val maxVisible = 3
    private val inflater = LayoutInflater.from(context)
    var currentTopIndex = startingIndex
    private var currentSwipeHandler: SwipeHandler? = null

    fun showInitialCards() {
        container.removeAllViews()
        for (i in 0 until minOf(maxVisible, allIdeas.size - currentTopIndex)) {
            addCardAt(i)
        }
    }

    private fun addCardAt(position: Int) {
        val dataIndex = currentTopIndex + position
        if (dataIndex >= allIdeas.size) return

        val idea = allIdeas[dataIndex]
        val card = inflater.inflate(R.layout.card_flip_container, container, false)

        val handler = SwipeHandler(card, 250f, 1000f) { direction ->
            val topCard = container.getChildAt(container.childCount - 1)
            if (card == topCard) {
                handleCardSwipe(card, idea, direction)
            }
        }
        handler.attach()

        val offset = 24 * position
        val scale = 1f - 0.03f * position
        card.translationY = offset.toFloat()
        card.scaleX = scale
        card.scaleY = scale
        card.elevation = (maxVisible - position).toFloat()

        card.post {
            card.x = (container.width - card.width) / 2f
        }

        container.addView(card, 0)

        if (position == 0) {
            currentSwipeHandler = handler
        }
        ProjectCardBinder.bind(card, context, idea, apiService)
    }

    private fun handleCardSwipe(card: View, idea: ProjectIdea, direction: Int) {
        container.removeView(card)
        currentTopIndex++

        if (currentTopIndex >= allIdeas.size) {
            currentTopIndex = 0
            showInitialCards()
            return
        }

        addCardAt(maxVisible - 1)
        restack()
        onCardSwiped(idea, direction)
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
                .setInterpolator(OvershootInterpolator())
                .withLayer()
                .start()

            card.elevation = (maxVisible - i).toFloat()

            val ideaIndex = currentTopIndex + i
            if (ideaIndex < allIdeas.size) {
                val handler = SwipeHandler(card, 250f, 1000f) { direction ->
                    val topCard = container.getChildAt(container.childCount - 1)
                    if (card == topCard) {
                        handleCardSwipe(card, allIdeas[ideaIndex], direction)
                    }
                }
                handler.attach()
                if (i == 0) {
                    currentSwipeHandler = handler
                }
            }
        }
    }

    fun updateIdeas(newIdeas: List<ProjectIdea>) {
        allIdeas = newIdeas
        currentTopIndex = 0
        showInitialCards()
    }
}
