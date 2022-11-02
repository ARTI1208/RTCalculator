package ru.art2000.calculator.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.R
import ru.art2000.extensions.getColorAttribute

abstract class SwipeToDeleteCallback(
    private val context: Context,
    private val isDeletable: (Int) -> Boolean,
    dragDirs: Int,
    swipeDirs: Int,
):  ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private val background by lazy { ColorDrawable(Color.RED) }
    private val xMark by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_clear_history)!!.apply {
            val whiteColorFilter: ColorFilter =
                PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            colorFilter = whiteColorFilter
        }
    }
    private val xMarkMargin by lazy {
        context.resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView
            val position = viewHolder.bindingAdapterPosition
            // not sure why, but this method gets called for viewholder that are already swiped away
            if (position == -1 || !isDeletable(position)) {
                // not interested in those
                return
            }

            val windowBackgroundColor = context.getColorAttribute(android.R.attr.colorBackground)
            itemView.setBackgroundColor(windowBackgroundColor)

            val itemHeight = itemView.bottom - itemView.top
            val intrinsicWidth = xMark.intrinsicWidth
            val intrinsicHeight = xMark.intrinsicWidth

            val xMarkLeft: Int
            val xMarkRight: Int
            if (dX > 0) {
                background.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt(), itemView.bottom
                )
                xMarkLeft = itemView.left + xMarkMargin
                xMarkRight = itemView.left + xMarkMargin + intrinsicWidth
            } else {
                background.setBounds(
                    itemView.right + dX.toInt(), itemView.top,
                    itemView.right, itemView.bottom
                )
                xMarkLeft = itemView.right - xMarkMargin - intrinsicWidth
                xMarkRight = itemView.right - xMarkMargin
            }
            val xMarkTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val xMarkBottom = xMarkTop + intrinsicHeight
            background.draw(c)

            // draw x mark
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom)
            xMark.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}