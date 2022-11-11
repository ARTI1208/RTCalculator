package ru.art2000.extensions.collections

import androidx.recyclerview.widget.DiffUtil

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