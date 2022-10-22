package ru.art2000.calculator.model.calculator.numbers

interface FloatingPointField<T> : NumberField<T> {

    fun isFloatingPointSymbol(c: Char): Boolean {
        return c == '.' || c == ','
    }
}

fun NumberField<*>.isFloatingPointSymbol(c: Char): Boolean {
    return if (this is FloatingPointField) isFloatingPointSymbol(c) else false
}