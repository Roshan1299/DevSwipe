package com.first.projectswipe.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.first.projectswipe.R
import com.first.projectswipe.models.ProjectIdea
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs

class HomeFragment : Fragment() {

    private lateinit var cardContainer: FrameLayout
    private val db = FirebaseFirestore.getInstance()
    private val projectIdeas = mutableListOf<ProjectIdea>()
    private val maxVisible = 3
    private val swipeThreshold = 250f
    private val flingVelocityThreshold = 1000f

    private var downX = 0f
    private var downY = 0f
    private var velocityTracker: VelocityTracker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        cardContainer = view.findViewById(R.id.cardStackContainer)

        (requireActivity() as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))

        view.findViewById<View>(R.id.fab_create_project).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createProjectIdeaFragment)
        }

        loadIdeas()
        return view
    }

    private fun loadIdeas() {
        db.collection("project_ideas")
            .get()
            .addOnSuccessListener { result ->
                projectIdeas.clear()
                projectIdeas.addAll(result.map { it.toObject(ProjectIdea::class.java) })
                showCards()
            }
            .addOnFailureListener {
                Log.e("HomeFragment", "Failed to load ideas", it)
            }
    }

    private fun showCards() {
        cardContainer.removeAllViews()
        val visibleCards = projectIdeas.take(maxVisible)

        visibleCards.reversed().forEachIndexed { index, idea ->
            val card = layoutInflater.inflate(R.layout.item_project_card, cardContainer, false)
            bindCard(card, idea)

            val offset = 24 * index
            val scale = 1f - 0.03f * index
            card.translationY = offset.toFloat()
            card.scaleX = scale
            card.scaleY = scale
            card.elevation = (maxVisible - index).toFloat()

            card.post {
                card.x = (cardContainer.width - card.width) / 2f
            }

            if (index == 0) setupTouch(card, idea)
            cardContainer.addView(card)
        }

        cardContainer.post {
            for (i in 0 until cardContainer.childCount) {
                val c = cardContainer.getChildAt(i)
                c.translationY = 200f + i * 24
                c.alpha = 0f
                c.animate()
                    .translationY((24 * i).toFloat())
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun bindCard(card: View, idea: ProjectIdea) {
        card.findViewById<TextView>(R.id.projectTitleTextView).text = idea.title
        card.findViewById<TextView>(R.id.projectDescriptionTextView).text = idea.description

        val chipGroup = card.findViewById<ChipGroup>(R.id.projectTagsChipGroup)
        chipGroup.removeAllViews()
        idea.tags?.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag
                isCheckable = false
                isClickable = false
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupTouch(card: View, idea: ProjectIdea) {
        card.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    velocityTracker = VelocityTracker.obtain()
                    velocityTracker?.addMovement(event)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    velocityTracker?.addMovement(event)
                    val dx = event.rawX - downX
                    val dy = event.rawY - downY
                    v.translationX = dx
                    v.translationY = dy
                    v.rotation = (dx / 20).coerceIn(-15f, 15f)
                    v.alpha = 1f - (abs(dx) / v.width * 1.5f).coerceIn(0f, 0.7f)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    velocityTracker?.computeCurrentVelocity(1000)
                    val xVelocity = velocityTracker?.getXVelocity(event.getPointerId(0)) ?: 0f
                    velocityTracker?.recycle()
                    velocityTracker = null

                    val dx = event.rawX - downX
                    val isFling = abs(xVelocity) > flingVelocityThreshold
                    val isSwipe = abs(dx) > swipeThreshold

                    if (isSwipe || isFling) {
                        val direction = if (dx > 0 || xVelocity > 0) 1 else -1
                        animateOffScreen(v, direction, idea)
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    } else {
                        v.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .rotation(0f)
                            .alpha(1f)
                            .setDuration(200)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    v.animate()
                                        .translationY(-10f)
                                        .setDuration(100)
                                        .withEndAction {
                                            v.animate()
                                                .translationY(0f)
                                                .setDuration(100)
                                                .start()
                                        }.start()
                                }
                            }).start()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun animateOffScreen(card: View, direction: Int, idea: ProjectIdea) {
        val targetX = direction * cardContainer.width * 1.5f
        card.animate()
            .translationX(targetX)
            .translationY(card.translationY + 150f)
            .rotation(20f * direction)
            .alpha(0f)
            .setDuration(250)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    cardContainer.removeView(card)
                    projectIdeas.remove(idea)
                    showCards()
                }
            }).start()
    }
}
