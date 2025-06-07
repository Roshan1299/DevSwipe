package com.first.projectswipe.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.first.projectswipe.R
import kotlin.math.abs

class SwipeHandler(
    private val card: View,
    private val swipeThreshold: Float,
    private val flingThreshold: Float,
    private val onSwipeComplete: (direction: Int) -> Unit
) {
    private var downX = 0f
    private var downY = 0f
    private var velocityTracker: VelocityTracker? = null

    fun attach() {
        card.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x  // 🔄 use local coordinates
                    downY = event.y
                    velocityTracker = VelocityTracker.obtain().apply {
                        addMovement(event)
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    velocityTracker?.addMovement(event)
                    val dx = event.x - downX
                    val dy = event.y - downY

                    v.translationX = dx
                    v.translationY = dy
                    v.rotation = (dx / 20).coerceIn(-15f, 15f)
                    v.alpha = 1f - (abs(dx) / v.width * 1.5f).coerceIn(0f, 0.7f)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val upX = event.x
                    val upY = event.y
                    val dx = upX - downX
                    val dy = upY - downY

                    velocityTracker?.computeCurrentVelocity(1000)
                    val xVelocity = velocityTracker?.getXVelocity(event.getPointerId(0)) ?: 0f
                    velocityTracker?.recycle()
                    velocityTracker = null

                    val isSwipe = abs(dx) > swipeThreshold || abs(xVelocity) > flingThreshold
                    val isHorizontalDominant = abs(dx) > abs(dy)

                    val direction = if (isHorizontalDominant) {
                        if (dx < 0 || xVelocity < 0) -1 else 1
                    } else {
                        0
                    }

                    if (isSwipe && direction != 0) {
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        animateOffScreen(v, direction)
                    } else {
                        v.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .rotation(0f)
                            .alpha(1f)
                            .setDuration(200)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .start()
                    }
                    true
                }

                else -> false
            }
        }

        card.setTag(R.id.swipe_handler_tag, this)
    }

    private fun animateOffScreen(view: View, direction: Int) {
        val targetX = direction * view.width * 1.5f
        view.animate()
            .translationX(targetX)
            .translationY(view.translationY + 150f)
            .rotation(20f * direction)
            .alpha(0f)
            .setDuration(250)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onSwipeComplete(direction)
                }
            })
            .start()
    }

    fun swipeLeft() {
        animateOffScreen(card, -1)
    }

    fun swipeRight() {
        animateOffScreen(card, 1)
    }

}
