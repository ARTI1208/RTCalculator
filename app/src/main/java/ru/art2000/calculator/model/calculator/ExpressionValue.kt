package ru.art2000.calculator.model.calculator

data class ExpressionValue<O>(val value: O) : ExpressionPart<O> {

    override fun toString() = value.toString()
}