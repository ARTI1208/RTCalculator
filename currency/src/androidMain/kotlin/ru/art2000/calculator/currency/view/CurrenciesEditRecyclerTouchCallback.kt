package ru.art2000.calculator.currency.view

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.common.view.SwipeToDeleteCallback

internal class CurrenciesEditRecyclerTouchCallback(
    context: Context,
    isMultiColumn: Boolean,
    private val onRemoved: (Int) -> Unit,
    private val onSwapped: (Int, Int) -> Unit
) : SwipeToDeleteCallback(
    context,
    { true },
    (if (isMultiColumn)ItemTouchHelper.START or ItemTouchHelper.END else 0)
            or ItemTouchHelper.DOWN or ItemTouchHelper.UP,
    ItemTouchHelper.START,
) {

    override fun isLongPressDragEnabled(): Boolean {
        return false
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