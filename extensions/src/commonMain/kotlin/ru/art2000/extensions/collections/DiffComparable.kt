package ru.art2000.extensions.collections

interface DiffComparable<T> {

    fun isSameItem(anotherItem: T): Boolean

    fun isContentSame(anotherItem: T): Boolean

}