package com.first.projectswipe.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import com.first.projectswipe.R
import com.first.projectswipe.data.models.CollabPost
import com.first.projectswipe.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollabCardStackManager(
    private val context: Context,
    private val container: FrameLayout,
    var allCollabPosts: List<CollabPost>,
    private val apiService: ApiService,
    private val fragment: androidx.fragment.app.Fragment,
    private val startingIndex: Int = 0,
    private val currentUserId: String? = null,
    private val onCardSwiped: (CollabPost, Int) -> Unit
) {
    private val maxVisible = 3
    private val inflater = LayoutInflater.from(context)
    var currentTopIndex = startingIndex
    private var currentSwipeHandler: SwipeHandler? = null

    fun showInitialCards() {
        container.removeAllViews()
        for (i in 0 until minOf(maxVisible, allCollabPosts.size - currentTopIndex)) {
            addCardAt(i)
        }
    }

    private fun addCardAt(position: Int) {
        val dataIndex = currentTopIndex + position
        if (dataIndex >= allCollabPosts.size) return

        val collabPost = allCollabPosts[dataIndex]
        val card = inflater.inflate(R.layout.card_flip_container, container, false)

        val handler = SwipeHandler(card, 250f, 1000f) { direction ->
            val topCard = container.getChildAt(container.childCount - 1)
            if (card == topCard) {
                handleCardSwipe(card, collabPost, direction)
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
        CollabCardBinder.bind(card, context, collabPost, apiService, fragment)
    }

    private fun handleCardSwipe(card: View, collabPost: CollabPost, direction: Int) {
        // direction > 0 means swipe right = apply
        if (direction > 0 && currentUserId != null && collabPost.createdBy.id != currentUserId) {
            applyToPost(collabPost)
        }

        container.removeView(card)
        currentTopIndex++

        if (currentTopIndex >= allCollabPosts.size) {
            currentTopIndex = 0
            showInitialCards()
            return
        }

        addCardAt(maxVisible - 1)
        restack()
        onCardSwiped(collabPost, direction)
    }

    private fun applyToPost(collabPost: CollabPost) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.applyToCollabPost(collabPost.id.toString())
                if (response.isSuccessful) {
                    Toast.makeText(context, "Applied to ${collabPost.projectTitle}!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("CollabStack", "Apply failed: $errorBody")
                    Toast.makeText(context, "Already applied or unavailable", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CollabStack", "Apply error: ${e.message}", e)
            }
        }
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

            val collabPostIndex = currentTopIndex + i
            if (collabPostIndex < allCollabPosts.size) {
                val handler = SwipeHandler(card, 250f, 1000f) { direction ->
                    val topCard = container.getChildAt(container.childCount - 1)
                    if (card == topCard) {
                        handleCardSwipe(card, allCollabPosts[collabPostIndex], direction)
                    }
                }
                handler.attach()
                if (i == 0) {
                    currentSwipeHandler = handler
                }
                CollabCardBinder.bind(card, context, allCollabPosts[collabPostIndex], apiService, fragment)
            }
        }
    }

    fun updateCollabPosts(newCollabPosts: List<CollabPost>) {
        allCollabPosts = newCollabPosts
        currentTopIndex = 0
        showInitialCards()
    }
}
