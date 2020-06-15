package ru.art2000.calculator.model.common

import androidx.recyclerview.widget.DiffUtil

object GlobalDependencies {

    @JvmStatic
    fun <T : DiffComparable<T>> getDiffCallback(oldData: List<T>, newData: List<T>): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
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
        }
    }

    @JvmStatic
    fun <T : DiffComparable<T>> calculateDiff(oldData: List<T>, newData: List<T>): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(getDiffCallback(oldData, newData))
    }

}