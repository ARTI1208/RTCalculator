package ru.art2000.calculator.model.calculator.numbers

interface CalculationNumber<T> {

    val field: NumberField<T>

    val value: T

    val isInfinite: Boolean

}