package ru.art2000.calculator.model.calculator

sealed class Operation<O> : ExpressionPart<O> {

    abstract val textRepresentations: Set<String>

    override fun toString(): String {
        return "'${textRepresentations.first()}'[${javaClass.simpleName}]"
    }
}

class BinaryOperation<O>(
        override val textRepresentations: Set<String>,
        private val inv: (O, O) -> O?,
        val priority: Int
) : Operation<O>() {

    constructor(textRepresentation: String, inv: (O, O) -> O?, priority: Int) :
            this(setOf(textRepresentation), inv, priority)

    operator fun invoke(leftOperand: O, rightOperand: O) = inv(leftOperand, rightOperand)

}

sealed class UnaryOperation<O>(
        override val textRepresentations: Set<String>,
        private val inv: (O) -> O?,
        val isTrigonometryOperation: Boolean = false
) : Operation<O>() {

    operator fun invoke(operand: O) = inv(operand)

}

class PrefixOperation<O>(
        textRepresentations: Set<String>, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
) : UnaryOperation<O>(textRepresentations, inv, isTrigonometryOperation) {

    constructor(
            textRepresentation: String, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
    ) : this(setOf(textRepresentation), inv, isTrigonometryOperation)

}

class PostfixOperation<O>(
        textRepresentations: Set<String>, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
) : UnaryOperation<O>(textRepresentations, inv, isTrigonometryOperation) {

    constructor(
            textRepresentation: String, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
    ) : this(setOf(textRepresentation), inv, isTrigonometryOperation)

}

val <O> PrefixOperation<O>.isSignOperation: Boolean
    get() = textRepresentations.contains("+") || textRepresentations.contains("-")