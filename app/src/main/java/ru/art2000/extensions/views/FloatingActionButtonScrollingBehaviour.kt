package ru.art2000.extensions.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.LinearInterpolator

class FloatingActionButtonScrollingBehaviour(context: Context, attrs: AttributeSet?) :
    FloatingActionButton.Behavior(context, attrs) {

    private var state = SHOWN
    private var isListenerAdded = false

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        if (!isListenerAdded) {
            isListenerAdded = true
            child.addOnShowAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    child.translationY = 0f
                    state = SHOWN
                }

                override fun onAnimationEnd(animator: Animator) {}
                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
        }
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        setupAnimation(child, dyConsumed)
        super.onNestedScroll(
            coordinatorLayout, child, target, dxConsumed, dyConsumed,
            dxUnconsumed, dyUnconsumed, type, consumed
        )
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dx: Int, dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        setupAnimation(child, dy)
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    private fun setupAnimation(child: FloatingActionButton, dy: Int) {
        if (dy > SCROLL_OFFSET && state == SHOWN) {
            state = ANIMATING_DOWN
            val layoutParams = child.layoutParams as MarginLayoutParams
            child.animate()
                .translationY((child.height + layoutParams.bottomMargin).toFloat())
                .setInterpolator(LinearInterpolator())
                .setDuration(ANIMATION_DURATION)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}
                    override fun onAnimationEnd(animator: Animator) {
                        state = HIDDEN
                    }

                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                }).start()
        } else if (dy < -SCROLL_OFFSET && state == HIDDEN) {
            state = ANIMATING_UP
            child.animate()
                .translationY(0f)
                .setInterpolator(LinearInterpolator())
                .setDuration(ANIMATION_DURATION)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}
                    override fun onAnimationEnd(animator: Animator) {
                        state = SHOWN
                    }

                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                }).start()
        }
    }

    companion object {
        private const val SHOWN = 0
        private const val HIDDEN = 1
        private const val ANIMATING_DOWN = 2
        private const val ANIMATING_UP = 3
        private const val ANIMATION_DURATION: Long = 100
        private const val SCROLL_OFFSET = 1
    }
}