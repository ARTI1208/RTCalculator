package ru.art2000.calculator.model.calculator.parts

sealed interface ExpressionPart<out O> {

    fun partAsString(): String

    fun mayComeAfter(part: ExpressionPart<@UnsafeVariance O>?): Boolean

}