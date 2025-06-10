package com.first.projectswipe.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
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
    private var isTracking = false
    private var velocityTracker: VelocityTracker? = null
    private val label: TextView? = card.findViewById(R.id.swipeFeedbackLabel)
    private val resetInterpolator = OvershootInterpolator(1.5f)

    fun attach() {
        card.isHapticFeedbackEnabled = true
        card.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().cancel()
                    downX = event.rawX
                    downY = event.rawY
                    isTracking = true
                    velocityTracker = VelocityTracker.obtain()
                    velocityTracker?.addMovement(event)
                    card.parent?.requestDisallowInterceptTouchEvent(true)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!isTracking) return@setOnTouchListener false

                    velocityTracker?.addMovement(event)
                    val dx = event.rawX - downX
                    val dy = event.rawY - downY

                    // Only process if movement is significant
                    if (abs(dx) > 5 || abs(dy) > 5) {
                        v.translationX = dx
                        v.rotation = (dx / 20).coerceIn(-15f, 15f)

                        // Only move Y if we're not in a clear horizontal swipe
                        if (abs(dx) < swipeThreshold * 0.5f) {
                            v.translationY = dy * 0.5f
                        }

                        updateSwipeFeedback(dx)
                    }
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (!isTracking) return@setOnTouchListener false
                    isTracking = false

                    velocityTracker?.computeCurrentVelocity(1000)
                    val xVelocity = velocityTracker?.xVelocity ?: 0f
                    velocityTracker?.recycle()
                    velocityTracker = null

                    val dx = card.translationX
                    val isSwipe = abs(dx) > swipeThreshold || abs(xVelocity) > flingThreshold
                    val direction = if (dx < 0 || xVelocity < 0) -1 else 1

                    label?.alpha = 0f

                    if (isSwipe && abs(dx) > 10f) {
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        animateOffScreen(v, direction)
                    } else {
                        resetCardPosition(v)
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun updateSwipeFeedback(dx: Float) {
        label?.let {
            val percent = (abs(dx) / swipeThreshold).coerceIn(0f, 1f)
            it.alpha = percent
            if (dx > 0) {
                it.text = "Like"
                it.setBackgroundColor(0xFF4CAF50.toInt()) // Green
            } else {
                it.text = "Nope"
                it.setBackgroundColor(0xFFF44336.toInt()) // Red
            }
        }
    }

    private fun resetCardPosition(view: View) {
        view.animate().cancel()
        view.animate()
            .translationX(0f)
            .translationY(0f)
            .rotation(0f)
            .setDuration(250)
            .setInterpolator(resetInterpolator)
            .withLayer()
            .start()
    }

    private fun animateOffScreen(view: View, direction: Int) {
        view.animate().cancel()

        label?.apply {
            text = if (direction > 0) "Like" else "Nope"
            setBackgroundColor(if (direction > 0) 0xFF4CAF50.toInt() else 0xFFF44336.toInt())
            alpha = 1f
        }

        view.animate()
            .translationX(direction * view.width * 1.5f)
            .translationY(view.translationY + 100f * direction)
            .rotation(15f * direction)
            .alpha(0.7f)
            .setDuration(200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withLayer()
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    label?.alpha = 0f
                    onSwipeComplete(direction)
                }
            })
            .start()
    }

    fun swipeLeft() = animateOffScreen(card, -1)
    fun swipeRight() = animateOffScreen(card, 1)
}