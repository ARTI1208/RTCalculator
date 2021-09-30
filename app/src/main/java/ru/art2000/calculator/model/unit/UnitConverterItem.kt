package ru.art2000.calculator.model.unit

import ru.art2000.calculator.model.common.DiffComparable

interface UnitConverterItem<T> : DiffComparable<UnitConverterItem<T>> {

    val currentValue: T

    val absoluteValue: T

    val nameResourceId: Int

    val shortNameResourceId: Int

    fun setValue(value: T)

    fun convert(from: UnitConverterItem<T>): T

    override fun isSameItem(anotherItem: UnitConverterItem<T>): Boolean {
        return nameResourceId == anotherItem.nameResourceId
    }

    override fun isContentSame(anotherItem: UnitConverterItem<T>): Boolean {
        return isSameItem(anotherItem) && currentValue == anotherItem.currentValue
    }
}