package ru.art2000.calculator.calculator.view

import android.graphics.Canvas
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.drawable.toDrawable

internal class HistoryItemDecoration : ItemDecoration() {

    // we want to cache this and not allocate anything repeatedly in the onDraw method
    private val background by lazy { Color.RED.toDrawable() }

    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        // only if animation is in progress
        val animator = parent.itemAnimator
        val layoutManager = parent.layoutManager

        if (animator != null && animator.isRunning && layoutManager != null) {

            // some items might be animating down and some items might be animating up to close the gap left by the removed item
            // this is not exclusive, both movement can be happening at the same time
            // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
            // then remove one from the middle

            // find first child with translationY > 0
            // and last one with translationY < 0
            // we're after a rect that is not covered in recycler-view views at this point in time
            var lastViewComingDown: View? = null
            var firstViewComingUp: View? = null

            // this is fixed
            val left = 0
            val right = parent.width

            // this we need to find out
            var top = 0
            var bottom = 0

            // find relevant translating views
            val childCount = layoutManager.childCount
            for (i in 0 until childCount) {
                val child = layoutManager.getChildAt(i) ?: continue
                if (child.translationY < 0) {
                    // view is coming down
                    lastViewComingDown = child
                } else if (child.translationY > 0) {
                    // view is coming up
                    if (firstViewComingUp == null) {
                        firstViewComingUp = child
                    }
                }
            }
            if (lastViewComingDown != null && firstViewComingUp != null) {
                // views are coming down AND going up to fill the void
                top = lastViewComingDown.bottom + lastViewComingDown.translationY.toInt()
                bottom = firstViewComingUp.top + firstViewComingUp.translationY.toInt()
            } else if (lastViewComingDown != null) {
                // views are going down to fill the void
                top = lastViewComingDown.bottom + lastViewComingDown.translationY.toInt()
                bottom = lastViewComingDown.bottom
            } else if (firstViewComingUp != null) {
                // views are coming up to fill the void
                top = firstViewComingUp.top
                bottom = firstViewComingUp.top + firstViewComingUp.translationY.toInt()
            }
            background.setBounds(left, top, right, bottom)
            background.draw(c)
        }

        super.onDraw(c, parent, state)
    }
}