package ru.art2000.calculator.view.calculator

import android.content.Context
import android.os.Build
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ItemHistoryDateBinding
import ru.art2000.calculator.databinding.ItemHistoryListBinding
import ru.art2000.calculator.model.calculator.history.HistoryDatabaseItem
import ru.art2000.calculator.model.calculator.history.HistoryDateItem
import ru.art2000.calculator.model.calculator.history.HistoryListItem
import ru.art2000.calculator.model.calculator.history.HistoryValueItem
import ru.art2000.calculator.view.calculator.HistoryListAdapter.HistoryViewHolder
import ru.art2000.calculator.view_model.calculator.HistoryViewModel
import ru.art2000.extensions.arch.launchAndCollect
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.collections.calculateDiff
import ru.art2000.extensions.views.textValue
import ru.art2000.extensions.views.toViewString

class HistoryListAdapter internal constructor(
        private val context: Context,
        lifecycleOwner: LifecycleOwner,
        private val model: HistoryViewModel,
        items: Flow<List<HistoryListItem>>
) : RecyclerView.Adapter<HistoryViewHolder>() {

    var historyList: List<HistoryListItem> = listOf()
        private set

    private val inflater = LayoutInflater.from(context)

    private fun setNewData(newData: List<HistoryListItem>) {
        if (historyList.isEmpty()) {
            historyList = newData
            notifyItemRangeInserted(0, newData.size)
        } else {
            val result = calculateDiff(historyList, newData)
            historyList = newData
            result.dispatchUpdatesTo(this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(historyList[position]) {
            is HistoryValueItem -> VALUE_ITEM
            is HistoryDateItem -> DATE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VALUE_ITEM -> {
            val binding = ItemHistoryListBinding.inflate(inflater, parent, false)
            ValueViewHolder(binding)
        }
        DATE_ITEM -> {
            val binding = ItemHistoryDateBinding.inflate(inflater, parent, false)
            DateViewHolder(binding)
        }
        else -> throw IllegalStateException("Unknown view type '$viewType'")
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.bind(item)
    }

    override fun onViewRecycled(holder: HistoryViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = historyList.size

    fun isDateItem(position: Int): Boolean = historyList.getOrNull(position) is HistoryDateItem

    abstract inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(historyListItem: HistoryListItem)
    }

    inner class ValueViewHolder internal constructor(private val binding: ItemHistoryListBinding) : HistoryViewHolder(binding.root), OnCreateContextMenuListener {

        private val currentDbItem: HistoryDatabaseItem
            get() = (historyList[bindingAdapterPosition] as HistoryValueItem).dbItem

        override fun bind(historyListItem: HistoryListItem) {
            require(historyListItem is HistoryValueItem)

            historyListItem.dbItem.apply {
                binding.expression.text = expression
                binding.result.text = result
                binding.comment.text = comment
                binding.comment.visibility = if (comment.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                         menuInfo: ContextMenuInfo?) {
            menu.setHeaderTitle(R.string.you_can)

            fun Menu.addHistoryMenuItem(itemId: Int, titleRes: Int) = add(
                    Menu.NONE,
                    itemId,
                    Menu.NONE,
                    titleRes,
            ).setOnMenuItemClickListener {
                onContextItemSelected(it)
            }

            menu.addHistoryMenuItem(HistoryViewModel.COMMENT, R.string.comment_item)
            menu.addHistoryMenuItem(HistoryViewModel.COPY_ALL, R.string.context_menu_copy_all)
            menu.addHistoryMenuItem(HistoryViewModel.COPY_EXPR, R.string.context_menu_copy_expr)
            menu.addHistoryMenuItem(HistoryViewModel.COPY_RES, R.string.context_menu_copy_res)
            menu.addHistoryMenuItem(HistoryViewModel.DELETE, R.string.delete_record)
        }

        private fun onContextItemSelected(menuItem: MenuItem): Boolean {
            val id = menuItem.itemId
            val isCopy = id >= HistoryViewModel.COPY_ALL && id <= HistoryViewModel.COPY_RES
            val toastText = if (isCopy) {
                model.copyHistoryItemToClipboard(currentDbItem, id)
            } else if (id == HistoryViewModel.DELETE) {
                val selectedItem = currentDbItem
                model.removeHistoryItem(selectedItem)
                context.getString(R.string.deleted) + " " + selectedItem.fullExpression
            } else if (id == HistoryViewModel.COMMENT) {
                showCommentDialog()
                null
            } else return true

            // API 33+ features UI showing copied content, so skip toasts for them
            if (!isCopy || Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                toastText?.also { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }
            return true
        }

        private fun showCommentDialog() {

            val dbItem = currentDbItem
            val editText = EditText(context)
            editText.textValue = dbItem.comment ?: ""

            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.comment_item_title)
                .setView(editText)
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .setPositiveButton(R.string.ok) { _, _ ->
                    dbItem.comment = editText.text.toString().takeIf { it.isNotEmpty() }
                    model.updateHistoryItem(dbItem)
                }.show()
        }

        init {
            itemView.setOnCreateContextMenuListener(this)
        }
    }

    inner class DateViewHolder internal constructor(private val binding: ItemHistoryDateBinding) : HistoryViewHolder(binding.root) {

        override fun bind(historyListItem: HistoryListItem) {
            require(historyListItem is HistoryDateItem)

            binding.date.text = historyListItem.date.toViewString()
        }
    }

    init {
        lifecycleOwner.launchRepeatOnStarted {
            launchAndCollect(items) { setNewData(it) }
        }
    }

    companion object {
        const val VALUE_ITEM = 0
        const val DATE_ITEM = 1
    }
}