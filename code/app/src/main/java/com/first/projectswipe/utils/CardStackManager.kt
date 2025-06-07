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
    private val startingIndex: Int = 0,
    private val onCardSwiped: (ProjectIdea, Int) -> Unit
) {
    private val maxVisible = 3
    private val inflater = LayoutInflater.from(context)
    private var currentTopIndex = startingIndex
    private var currentSwipeHandler: SwipeHandler? = null

    fun showInitialCards() {
        container.removeAllViews()
        for (i in 0 until maxVisible) {
            addCardAt(i)
        }
    }

    private fun addCardAt(position: Int) {
        val dataIndex = currentTopIndex + position
        if (dataIndex >= allIdeas.size) return

        val idea = allIdeas[dataIndex]
        val card = inflater.inflate(R.layout.card_flip_container, container, false)

        // ✅ Attach SwipeHandler for every card
        val handler = SwipeHandler(card, 250f, 1000f) { direction ->
            if (position == 0) { // Only the top card should trigger swipe actions
                container.removeView(card)
                currentTopIndex++

                saveSwipeProgress(currentTopIndex)

                if (currentTopIndex >= allIdeas.size) {
                    resetSwipeProgress()
                    currentTopIndex = 0
                    showInitialCards()
                    return@SwipeHandler
                }

                addCardAt(maxVisible - 1)
                restack()
                onCardSwiped(idea, direction)
            }
        }
        handler.attach()

        // ✅ Bind after swipe handler is attached so tag is set
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

        container.addView(card, 0)

        // ✅ Only store reference to top swipe handler
        if (position == 0) currentSwipeHandler = handler
    }



    private fun setupSwipe(card: View, idea: ProjectIdea) {
        val handler = SwipeHandler(card, 250f, 1000f) { direction ->
            container.removeView(card)
            currentTopIndex++

            saveSwipeProgress(currentTopIndex)

            if (currentTopIndex >= allIdeas.size) {
                resetSwipeProgress()
                currentTopIndex = 0
                showInitialCards()
                return@SwipeHandler
            }

            addCardAt(maxVisible - 1)
            restack()
            onCardSwiped(idea, direction)
        }
        handler.attach()
        currentSwipeHandler = handler
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

    private fun saveSwipeProgress(index: Int) {
        context.getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("swipe_index", index)
            .apply()
    }

    private fun resetSwipeProgress() {
        context.getSharedPreferences("SwipePrefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("swipe_index", 0)
            .apply()
    }

    fun resetToStart() {
        currentTopIndex = 0
        container.removeAllViews()
        showInitialCards()
    }

    fun swipeLeft() {
        currentSwipeHandler?.swipeLeft()
    }

    fun swipeRight() {
        currentSwipeHandler?.swipeRight()
    }
}
