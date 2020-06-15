package ru.art2000.calculator.unit_converter.model

import ru.art2000.calculator.model.DiffComparable

interface UnitConverterItem : DiffComparable<UnitConverterItem> {

    val currentValue: Double

    val absoluteValue: Double

    val nameResourceId: Int

    fun setValue(value: Double)

    fun convert(from: UnitConverterItem): Double

    override fun isSameItem(anotherItem: UnitConverterItem): Boolean {
        return nameResourceId == anotherItem.nameResourceId
    }

    override fun isContentSame(anotherItem: UnitConverterItem): Boolean {
        return isSameItem(anotherItem) && currentValue == anotherItem.currentValue
    }
}