package ru.art2000.calculator.model.unit

import ru.art2000.extensions.collections.DiffComparable

interface UnitConverterItem<T> : UnitItem, DiffComparable<UnitConverterItem<T>> {

    val isSet: Boolean

    val currentValue: T

    val absoluteValue: T

    fun setValue(value: T)

    fun convert(from: UnitConverterItem<T>): T

    override fun isSameItem(anotherItem: UnitConverterItem<T>): Boolean {
        return nameResourceId == anotherItem.nameResourceId
    }

    override fun isContentSame(anotherItem: UnitConverterItem<T>): Boolean {
        return isSameItem(anotherItem) && currentValue == anotherItem.currentValue
    }
}