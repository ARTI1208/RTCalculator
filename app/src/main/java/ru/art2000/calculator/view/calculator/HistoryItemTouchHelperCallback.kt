package ru.art2000.calculator.view.calculator

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.view.SwipeToDeleteCallback

internal class HistoryItemTouchHelperCallback(
    context: Context,
    private val isSwipeable: (Int) -> Boolean,
    private val onItemSwiped: (Int) -> Unit,
) : SwipeToDeleteCallback(
    context,
    isSwipeable,
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (isSwipeable(position)) {
            onItemSwiped(position)
        }
    }
}