package ru.art2000.calculator.calculator.computation.numbers

interface CalculationNumber<T> {

    val value: T

    val isInfinite: Boolean

}