package ru.art2000.calculator.view.currency

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.R
import ru.art2000.helpers.getColorAttribute

class CurrenciesEditRecyclerTouchCallback(
        private val mContext: Context,
        private val onRemoved: (Int) -> Unit,
        private val onSwapped: (Int, Int) -> Unit
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, ItemTouchHelper.START) {

    private var background: Drawable? = null
    private var xMark: Drawable? = null
    private var xMarkMargin = 0.0f
    private var initiated = false

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    private fun init() {
        background = ColorDrawable(Color.RED)
        xMark = ContextCompat.getDrawable(mContext, R.drawable.ic_clear_history)
        val filter: ColorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        xMark!!.colorFilter = filter
        xMarkMargin = mContext.resources.getDimension(R.dimen.activity_horizontal_margin)
        initiated = true
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {
        if (dY == 0f) {
            val itemView = viewHolder.itemView
            if (viewHolder.bindingAdapterPosition == -1) {
                return
            }
            if (!initiated) {
                init()
            }
            val windowBackgroundColor = mContext.getColorAttribute(android.R.attr.colorBackground)
            itemView.setBackgroundColor(windowBackgroundColor)
            val itemHeight = itemView.bottom - itemView.top
            val intrinsicWidth = xMark!!.intrinsicWidth
            val intrinsicHeight = xMark!!.intrinsicWidth
            val xMarkLeft: Int
            val xMarkRight: Int
            val xMarkTop: Int
            val xMarkBottom: Int
            if (dX > 0) {
                background!!.setBounds(itemView.left, itemView.top,
                        itemView.left + dX.toInt(), itemView.bottom)
                xMarkLeft = (itemView.left + xMarkMargin).toInt()
                xMarkRight = (itemView.left + xMarkMargin + intrinsicWidth).toInt()
            } else {
                background!!.setBounds(itemView.right + dX.toInt(), itemView.top,
                        itemView.right, itemView.bottom)
                xMarkLeft = (itemView.right - xMarkMargin - intrinsicWidth).toInt()
                xMarkRight = (itemView.right - xMarkMargin).toInt()
            }
            xMarkTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            xMarkBottom = xMarkTop + intrinsicHeight
            background!!.draw(c)
            xMark!!.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom)
            xMark!!.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onMove(recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {

        onSwapped(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        onRemoved(position)
    }

}