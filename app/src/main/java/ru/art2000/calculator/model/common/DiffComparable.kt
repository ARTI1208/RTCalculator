package ru.art2000.calculator.model.common

interface DiffComparable<T> {

    fun isSameItem(anotherItem: T): Boolean

    fun isContentSame(anotherItem: T): Boolean

}