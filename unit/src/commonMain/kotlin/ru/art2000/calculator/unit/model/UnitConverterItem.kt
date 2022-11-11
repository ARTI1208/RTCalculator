package ru.art2000.calculator.unit.model

internal interface UnitConverterItem<T> {

    val isSet: Boolean

    val currentValue: T

    val absoluteValue: T

    fun setValue(value: T)

    fun convert(from: UnitConverterItem<T>): T
}