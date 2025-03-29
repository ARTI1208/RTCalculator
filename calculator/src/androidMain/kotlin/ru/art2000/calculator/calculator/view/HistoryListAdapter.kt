package ru.art2000.calculator.calculator.view

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
import kotlinx.datetime.toJavaLocalDate
import ru.art2000.calculator.calculator.R
import ru.art2000.calculator.calculator.databinding.ItemHistoryDateBinding
import ru.art2000.calculator.calculator.databinding.ItemHistoryListBinding
import ru.art2000.calculator.calculator.model.HistoryDateItem
import ru.art2000.calculator.calculator.model.HistoryListItem
import ru.art2000.calculator.calculator.model.HistoryValueItem
import ru.art2000.calculator.calculator.view.HistoryListAdapter.HistoryViewHolder
import ru.art2000.calculator.calculator.vm.HistoryViewModel
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.collections.calculateDiff
import ru.art2000.extensions.kt.launchAndCollect
import ru.art2000.extensions.views.textValue
import ru.art2000.extensions.views.toViewString
import ru.art2000.calculator.common.R as CommonR

internal class HistoryListAdapter internal constructor(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
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

        private val currentItem: HistoryValueItem
            get() = nullableCurrentItem!!

        private val nullableCurrentItem: HistoryValueItem?
            get() = historyList.getOrNull(bindingAdapterPosition) as HistoryValueItem?

        private val HistoryValueItem.computedResult
            get() = model.ensureDisplayResult(this)

        override fun bind(historyListItem: HistoryListItem) {
            require(historyListItem is HistoryValueItem)

            historyListItem.apply {
                binding.expression.text = expression
                binding.result.text = computedResult
                binding.comment.text = comment
                binding.comment.visibility = if (comment.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                         menuInfo: ContextMenuInfo?) {
            menu.setHeaderTitle(CommonR.string.you_can)

            fun Menu.addHistoryMenuItem(itemId: Int, titleRes: Int) = add(
                    Menu.NONE,
                    itemId,
                    Menu.NONE,
                    titleRes,
            ).setOnMenuItemClickListener(::onContextItemSelected)

            fun Menu.addHistoryMenuItem(copyMode: HistoryViewModel.CopyMode, titleRes: Int) =
                addHistoryMenuItem(copyMode.id, titleRes)

            menu.addHistoryMenuItem(HistoryViewModel.COMMENT, R.string.comment_item)
            menu.addHistoryMenuItem(HistoryViewModel.CopyMode.ALL, R.string.context_menu_copy_all)
            menu.addHistoryMenuItem(HistoryViewModel.CopyMode.EXPRESSION, R.string.context_menu_copy_expr)
            menu.addHistoryMenuItem(HistoryViewModel.CopyMode.RESULT, R.string.context_menu_copy_res)
            menu.addHistoryMenuItem(HistoryViewModel.DELETE, R.string.delete_record)
        }

        @Suppress("SameReturnValue")
        private fun onContextItemSelected(menuItem: MenuItem): Boolean {
            val id = menuItem.itemId
            val copyMode = HistoryViewModel.CopyMode.fromId(id)
            val toastText = if (copyMode != null) {
                context.getString(CommonR.string.copied) + " " +
                        model.copyHistoryItemToClipboard(currentItem, copyMode)
            } else if (id == HistoryViewModel.DELETE) {
                val selectedItem = currentItem
                model.removeHistoryItem(selectedItem)
                val fullExpression = "${selectedItem.expression}=${binding.result.text}"
                context.getString(R.string.deleted) + " " + fullExpression
            } else if (id == HistoryViewModel.COMMENT) {
                showCommentDialog()
                null
            } else return true

            // API 33+ features UI showing copied content, so skip toasts for them
            if (copyMode == null || Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                toastText?.also { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }
            return true
        }

        private fun showCommentDialog() {

            val dbItem = currentItem
            val editText = EditText(context)
            editText.textValue = dbItem.comment ?: ""

            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.comment_item_title)
                .setView(editText)
                .setNegativeButton(CommonR.string.cancel) { _, _ -> }
                .setPositiveButton(CommonR.string.ok) { _, _ ->
                    dbItem.comment = editText.text.toString().takeIf { it.isNotEmpty() }
                    model.updateHistoryItem(dbItem)
                }.show()
        }

        init {
            itemView.setOnCreateContextMenuListener(this)
            lifecycleOwner.launchRepeatOnStarted {
                launchAndCollect(model.historyConfigChanged) {
                    val item = nullableCurrentItem ?: return@launchAndCollect
                    binding.result.text = item.computedResult
                }
            }
        }
    }

    inner class DateViewHolder internal constructor(
        private val binding: ItemHistoryDateBinding,
    ) : HistoryViewHolder(binding.root) {

        override fun bind(historyListItem: HistoryListItem) {
            require(historyListItem is HistoryDateItem)

            binding.date.text = historyListItem.date.toJavaLocalDate().toViewString()
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