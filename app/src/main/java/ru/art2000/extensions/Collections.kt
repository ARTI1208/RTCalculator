package ru.art2000.extensions

import androidx.recyclerview.widget.DiffUtil
import ru.art2000.calculator.model.common.DiffComparable
import java.util.*
import kotlin.collections.LinkedHashMap

public inline fun <K, V> Iterable<K>.indexedAssociateWith(valueSelector: (Int, K) -> V): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    return indexedAssociateWithTo(result, valueSelector)
}

public inline fun <K, V> Iterable<V>.indexedAssociateBy(valueSelector: (Int, V) -> K): Map<K, V> {
    val result = LinkedHashMap<K, V>()
    return indexedAssociateByTo(result, valueSelector)
}

public inline fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.indexedAssociateWithTo(destination: M, valueSelector: (Int, K) -> V): M {
    for ((k, element) in this.withIndex()) {
        destination.put(element, valueSelector(k, element))
    }
    return destination
}

public inline fun <K, V, M : MutableMap<in K, in V>> Iterable<V>.indexedAssociateByTo(destination: M, valueSelector: (Int, V) -> K): M {
    for ((k, element) in this.withIndex()) {
        destination.put(valueSelector(k, element), element)
    }
    return destination
}

public fun <E> MutableCollection<E>.supportRemoveIf(filter: (E) -> Boolean): Boolean {
    var removed = false
    val each: MutableIterator<E> = iterator()
    while (each.hasNext()) {
        if (filter(each.next())) {
            each.remove()
            removed = true
        }
    }
    return removed
}

public fun <E> MutableCollection<E>.extendedRemoveIf(filter: (E) -> Boolean, action: (E)  -> Unit): Boolean {
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

public fun <T : DiffComparable<T>> calculateDiff(oldData: List<T>, newData: List<T>): DiffUtil.DiffResult {
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