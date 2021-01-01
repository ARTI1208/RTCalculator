package ru.art2000.extensions.collections

import androidx.recyclerview.widget.DiffUtil
import ru.art2000.calculator.model.common.DiffComparable

fun <E> MutableCollection<E>.extendedRemoveIf(filter: (E) -> Boolean, action: (E) -> Unit): Boolean {
    var removed = false
    val each: MutableIterator<E> = iterator()
    while (each.hasNext()) {
        val element = each.next()
        if (filter(element)) {
            each.remove()
            action(element)
            removed = true
        }
    }
    return removed
}

fun <T : DiffComparable<T>> calculateDiff(oldData: List<T>, newData: List<T>): DiffUtil.DiffResult {
    return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition].isSameItem(newData[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition].isContentSame(newData[newItemPosition])
        }
    })
}