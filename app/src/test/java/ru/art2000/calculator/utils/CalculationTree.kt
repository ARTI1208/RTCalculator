package ru.art2000.calculator.model.calculator

import ru.art2000.calculator.model.calculator.parts.*

sealed class CalculationTree<O> {

    abstract operator fun invoke(): O?
}

sealed class OperationTree<O> : CalculationTree<O>() {

    abstract val operation: Operation<O>

}

class BinaryOperationTree<O>(
    override val operation: BinaryOperation<O>,
    private val leftTree: CalculationTree<O>,
    private val rightTree: CalculationTree<O>,
) : OperationTree<O>() {

    override operator fun invoke(): O? {
        val leftOperand = leftTree() ?: return null
        val rightOperand = rightTree() ?: return null

        return operation(leftOperand, rightOperand)
    }

    override fun toString(): String {
        return "$leftTree${operation.textRepresentations.first()}$rightTree"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BinaryOperationTree<*>) return false

        if (operation != other.operation) return false
        if (leftTree != other.leftTree) return false
        if (rightTree != other.rightTree) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operation.hashCode()
        result = 31 * result + leftTree.hashCode()
        result = 31 * result + rightTree.hashCode()
        return result
    }

}

sealed class UnaryOperationTree<O>(
    override val operation: UnaryOperation<O>,
    protected val subTree: CalculationTree<O>,
    private val subTreeConverter: (O) -> O
) : OperationTree<O>() {

    override operator fun invoke(): O? {
        val operand = subTree() ?: return null

        return operation(subTreeConverter(operand))
    }

    abstract fun toString(brackets: Boolean): String

    override fun toString(): String = toString(true)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnaryOperationTree<*>) return false

        if (operation != other.operation) return false
        if (subTree != other.subTree) return false
        if (subTreeConverter != other.subTreeConverter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = operation.hashCode()
        result = 31 * result + subTree.hashCode()
        result = 31 * result + subTreeConverter.hashCode()
        return result
    }


}

class PrefixOperationTree<O>(
    operation: PrefixOperation<O>,
    subTree: CalculationTree<O>,
    subTreeConverter: (O) -> O = { it },
) : UnaryOperationTree<O>(operation, subTree, subTreeConverter) {

    override fun toString(brackets: Boolean): String {
        return operation.textRepresentations.first() + (if (brackets) "(" else "") + subTree.toString() + (if (brackets) ")" else "")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrefixOperationTree<*>) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}

class PostfixOperationTree<O>(
    operation: PostfixOperation<O>,
    subTree: CalculationTree<O>,
    subTreeConverter: (O) -> O = { it },
) : UnaryOperationTree<O>(operation, subTree, subTreeConverter) {

    override fun toString(brackets: Boolean): String {
        return (if (brackets) "(" else "") + subTree.toString() + (if (brackets) ")" else "") + operation.textRepresentations.first()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PostfixOperationTree<*>) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}

class ConstantTree<O>(private val constant: O) : CalculationTree<O>() {

    override fun invoke(): O = constant

    override fun toString(): String = constant.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConstantTree<*>) return false

        if (constant != other.constant) return false

        return true
    }

    override fun hashCode(): Int {
        return constant?.hashCode() ?: 0
    }
}