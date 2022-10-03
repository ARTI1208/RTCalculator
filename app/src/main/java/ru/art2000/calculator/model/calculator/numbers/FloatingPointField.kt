package ru.art2000.calculator.model.calculator.numbers

interface FloatingPointField<T> : NumberField<T> {

    fun isFloatingPointSymbol(c: Char): Boolean

}