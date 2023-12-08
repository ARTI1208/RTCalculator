package ru.art2000.extensions.collections

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