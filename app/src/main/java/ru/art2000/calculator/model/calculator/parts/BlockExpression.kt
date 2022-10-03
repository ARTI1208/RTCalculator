package ru.art2000.calculator.model.calculator.parts

class BlockOpenExpression<O> : ExpressionPart<O> {

    override fun toString() = "("

    override fun equals(other: Any?) = other is BlockOpenExpression<*>

    override fun hashCode() = 123

    override fun partAsString(): String = "("

    override fun mayComeAfter(part: ExpressionPart<O>?) =
        part == null || part is ValueBased || part is PostfixOperation
}

class BlockCloseExpression<O> : ExpressionPart<O> {

    override fun toString() = ")"

    override fun equals(other: Any?) = other is BlockCloseExpression<*>

    override fun hashCode() = 321

    override fun partAsString(): String = ")"

    override fun mayComeAfter(part: ExpressionPart<O>?) =
        part is ValueBased || part is PostfixOperation
}

val BlockOpenExpressionInstance = BlockOpenExpression<Any>()
val BlockCloseExpressionInstance = BlockCloseExpression<Any>()

@Suppress("UNCHECKED_CAST")
fun <O> blockOpen(): BlockOpenExpression<O> = BlockOpenExpressionInstance as BlockOpenExpression<O>

@Suppress("UNCHECKED_CAST")
fun <O> blockClose(): BlockCloseExpression<O> = BlockCloseExpressionInstance as BlockCloseExpression<O>