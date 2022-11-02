package ru.art2000.extensions.collections

import androidx.recyclerview.widget.DiffUtil

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

fun <E> List<E>.indexOf(e: E, fromIndex: Int = 0): Int {
    return indexOfFirst(fromIndex) { it == e }
}

inline fun <E> List<E>.indexOfFirst(fromIndex: Int = 0, predicate: (E) -> Boolean): Int {
    val index = fromIndex.coerceAtLeast(0)
    for (i in index until size) {
        if (predicate(this[i])) return i
    }
    return -1
}

fun <T : DiffComparable<T>> calculateDiff(oldData: List<T>, newData: List<T>) =
    calculateDiff(oldData, newData, DiffComparable<T>::isSameItem, DiffComparable<T>::isContentSame)

fun <T> calculateDiff(
    oldData: List<T>,
    newData: List<T>,
    isSameItem: T.(T) -> Boolean,
    isContentSame: T.(T) -> Boolean,
): DiffUtil.DiffResult {
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