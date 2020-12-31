package ru.art2000.calculator.model.calculator

class BlockOpenExpression<O> : ExpressionPart<O> {
    override fun toString(): String {
        return "("
    }

    override fun equals(other: Any?): Boolean {
        return other is BlockOpenExpression<*>
    }

    override fun hashCode(): Int {
        return 123
    }
}

class BlockCloseExpression<O> : ExpressionPart<O> {
    override fun toString(): String {
        return ")"
    }

    override fun equals(other: Any?): Boolean {
        return other is BlockCloseExpression<*>
    }

    override fun hashCode(): Int {
        return 321
    }
}