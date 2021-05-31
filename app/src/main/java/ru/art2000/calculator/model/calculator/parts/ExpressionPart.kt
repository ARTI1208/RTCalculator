package ru.art2000.calculator.model.calculator.parts

sealed interface ExpressionPart<O> {

    fun partAsString(): String

}