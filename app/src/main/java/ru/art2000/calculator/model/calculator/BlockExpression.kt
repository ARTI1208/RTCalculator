package ru.art2000.calculator.model.calculator

class BlockOpenExpression<O> : ExpressionPart<O> {

    override fun toString() = "("

    override fun equals(other: Any?) = other is BlockOpenExpression<*>

    override fun hashCode() = 123
}

class BlockCloseExpression<O> : ExpressionPart<O> {

    override fun toString() = ")"

    override fun equals(other: Any?) = other is BlockCloseExpression<*>

    override fun hashCode() = 321
}