package com.first.projectswipe.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.first.projectswipe.MainActivity
import com.first.projectswipe.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var logoImage: ImageView
    private lateinit var ball1: View
    private lateinit var ball2: View
    private lateinit var ball3: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logoImage = findViewById(R.id.logoImageView)
        ball1 = findViewById(R.id.ball1)
        ball2 = findViewById(R.id.ball2)
        ball3 = findViewById(R.id.ball3)

        // Initially hide logo and position balls
        logoImage.alpha = 0f
        setupInitialBallPositions()

        startAnimation()
    }

    private fun setupInitialBallPositions() {
        // Start balls in a line formation
        ball1.translationX = -120f
        ball2.translationX = 0f
        ball3.translationX = 120f

        ball1.translationY = 0f
        ball2.translationY = 0f
        ball3.translationY = 0f
    }

    private fun startAnimation() {
        lifecycleScope.launch {
            delay(300)

            // Phase 1: Balls entrance
            ballsEntrance()
            delay(800)

            // Phase 2: First reshuffle - triangle formation
            reshuffleToTriangle()
            delay(1000)

            // Phase 3: Second reshuffle - vertical line
            reshuffleToVertical()
            delay(1000)

            // Phase 4: Final reshuffle - merge and show logo
            mergeAndShowLogo()
            delay(800)

            // Phase 5: Navigate to main
            navigateToMainActivity()
        }
    }

    private fun ballsEntrance() {
        val ball1Scale = ObjectAnimator.ofFloat(ball1, "scaleX", 0f, 1.2f, 1f)
        val ball1ScaleY = ObjectAnimator.ofFloat(ball1, "scaleY", 0f, 1.2f, 1f)
        val ball1Alpha = ObjectAnimator.ofFloat(ball1, "alpha", 0f, 1f)

        val ball2Scale = ObjectAnimator.ofFloat(ball2, "scaleX", 0f, 1.2f, 1f)
        val ball2ScaleY = ObjectAnimator.ofFloat(ball2, "scaleY", 0f, 1.2f, 1f)
        val ball2Alpha = ObjectAnimator.ofFloat(ball2, "alpha", 0f, 1f)

        val ball3Scale = ObjectAnimator.ofFloat(ball3, "scaleX", 0f, 1.2f, 1f)
        val ball3ScaleY = ObjectAnimator.ofFloat(ball3, "scaleY", 0f, 1.2f, 1f)
        val ball3Alpha = ObjectAnimator.ofFloat(ball3, "alpha", 0f, 1f)

        val ball1Animator = AnimatorSet()
        ball1Animator.playTogether(ball1Scale, ball1ScaleY, ball1Alpha)
        ball1Animator.duration = 600
        ball1Animator.interpolator = BounceInterpolator()

        val ball2Animator = AnimatorSet()
        ball2Animator.playTogether(ball2Scale, ball2ScaleY, ball2Alpha)
        ball2Animator.duration = 600
        ball2Animator.startDelay = 200
        ball2Animator.interpolator = BounceInterpolator()

        val ball3Animator = AnimatorSet()
        ball3Animator.playTogether(ball3Scale, ball3ScaleY, ball3Alpha)
        ball3Animator.duration = 600
        ball3Animator.startDelay = 400
        ball3Animator.interpolator = BounceInterpolator()

        val entranceAnimator = AnimatorSet()
        entranceAnimator.playTogether(ball1Animator, ball2Animator, ball3Animator)
        entranceAnimator.start()
    }

    private fun reshuffleToTriangle() {
        // Move to triangle formation
        val ball1MoveX = ObjectAnimator.ofFloat(ball1, "translationX", -120f, -60f)
        val ball1MoveY = ObjectAnimator.ofFloat(ball1, "translationY", 0f, -80f)

        val ball2MoveX = ObjectAnimator.ofFloat(ball2, "translationX", 0f, 60f)
        val ball2MoveY = ObjectAnimator.ofFloat(ball2, "translationY", 0f, -80f)

        val ball3MoveX = ObjectAnimator.ofFloat(ball3, "translationX", 120f, 0f)
        val ball3MoveY = ObjectAnimator.ofFloat(ball3, "translationY", 0f, 80f)

        val triangleAnimator = AnimatorSet()
        triangleAnimator.playTogether(
            ball1MoveX, ball1MoveY,
            ball2MoveX, ball2MoveY,
            ball3MoveX, ball3MoveY
        )
        triangleAnimator.duration = 800
        triangleAnimator.interpolator = AccelerateDecelerateInterpolator()
        triangleAnimator.start()
    }

    private fun reshuffleToVertical() {
        // Move to vertical line formation
        val ball1MoveX = ObjectAnimator.ofFloat(ball1, "translationX", -60f, 0f)
        val ball1MoveY = ObjectAnimator.ofFloat(ball1, "translationY", -80f, -100f)

        val ball2MoveX = ObjectAnimator.ofFloat(ball2, "translationX", 60f, 0f)
        val ball2MoveY = ObjectAnimator.ofFloat(ball2, "translationY", -80f, 0f)

        val ball3MoveX = ObjectAnimator.ofFloat(ball3, "translationX", 0f, 0f)
        val ball3MoveY = ObjectAnimator.ofFloat(ball3, "translationY", 80f, 100f)

        val verticalAnimator = AnimatorSet()
        verticalAnimator.playTogether(
            ball1MoveX, ball1MoveY,
            ball2MoveX, ball2MoveY,
            ball3MoveX, ball3MoveY
        )
        verticalAnimator.duration = 800
        verticalAnimator.interpolator = AccelerateDecelerateInterpolator()
        verticalAnimator.start()
    }

    private fun mergeAndShowLogo() {
        // Balls merge to center and fade out
        val ball1MoveX = ObjectAnimator.ofFloat(ball1, "translationX", 0f, 0f)
        val ball1MoveY = ObjectAnimator.ofFloat(ball1, "translationY", -100f, 0f)
        val ball1Scale = ObjectAnimator.ofFloat(ball1, "scaleX", 1f, 0.3f)
        val ball1ScaleY = ObjectAnimator.ofFloat(ball1, "scaleY", 1f, 0.3f)
        val ball1Alpha = ObjectAnimator.ofFloat(ball1, "alpha", 1f, 0f)

        val ball2Scale = ObjectAnimator.ofFloat(ball2, "scaleX", 1f, 0.3f)
        val ball2ScaleY = ObjectAnimator.ofFloat(ball2, "scaleY", 1f, 0.3f)
        val ball2Alpha = ObjectAnimator.ofFloat(ball2, "alpha", 1f, 0f)

        val ball3MoveX = ObjectAnimator.ofFloat(ball3, "translationX", 0f, 0f)
        val ball3MoveY = ObjectAnimator.ofFloat(ball3, "translationY", 100f, 0f)
        val ball3Scale = ObjectAnimator.ofFloat(ball3, "scaleX", 1f, 0.3f)
        val ball3ScaleY = ObjectAnimator.ofFloat(ball3, "scaleY", 1f, 0.3f)
        val ball3Alpha = ObjectAnimator.ofFloat(ball3, "alpha", 1f, 0f)

        // Logo appears
        val logoAlpha = ObjectAnimator.ofFloat(logoImage, "alpha", 0f, 1f)
        val logoScale = ObjectAnimator.ofFloat(logoImage, "scaleX", 0.8f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logoImage, "scaleY", 0.8f, 1f)

        val mergeAnimator = AnimatorSet()
        mergeAnimator.playTogether(
            ball1MoveX, ball1MoveY, ball1Scale, ball1ScaleY, ball1Alpha,
            ball2Scale, ball2ScaleY, ball2Alpha,
            ball3MoveX, ball3MoveY, ball3Scale, ball3ScaleY, ball3Alpha
        )
        mergeAnimator.duration = 600
        mergeAnimator.interpolator = AccelerateDecelerateInterpolator()

        val logoAnimator = AnimatorSet()
        logoAnimator.playTogether(logoAlpha, logoScale, logoScaleY)
        logoAnimator.duration = 800
        logoAnimator.startDelay = 300
        logoAnimator.interpolator = OvershootInterpolator()

        val finalAnimator = AnimatorSet()
        finalAnimator.playTogether(mergeAnimator, logoAnimator)
        finalAnimator.start()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onBackPressed() {
        // Disable back button during splash
    }
}