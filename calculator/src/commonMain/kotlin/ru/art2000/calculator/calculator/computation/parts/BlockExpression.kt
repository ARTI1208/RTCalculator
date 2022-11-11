package ru.art2000.calculator.calculator.computation.parts

internal class BlockOpenExpression<O> : ExpressionPart<O> {

    override fun toString() = "("

    override fun equals(other: Any?) = other is BlockOpenExpression<*>

    override fun hashCode() = 123

    override fun partAsString(): String = "("
}

internal class BlockCloseExpression<O> : ExpressionPart<O> {

    override fun toString() = ")"

    override fun equals(other: Any?) = other is BlockCloseExpression<*>

    override fun hashCode() = 321

    override fun partAsString(): String = ")"
}