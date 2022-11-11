package ru.art2000.calculator.calculator.computation.parts

internal sealed interface ExpressionPart<out O> {

    fun partAsString(): String

}