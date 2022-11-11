package ru.art2000.calculator.calculator.computation.parts

internal sealed class Operation<out O> : ExpressionPart<O> {

    abstract val textRepresentations: Set<String>

    override fun toString(): String {
        return "'${textRepresentations.first()}'[${this::class.simpleName}]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Operation<*>) return false

        if (textRepresentations != other.textRepresentations) return false

        return true
    }

    override fun hashCode(): Int {
        return textRepresentations.hashCode()
    }


}

internal class BinaryOperation<O>(
        override val textRepresentations: Set<String>,
        private val inv: (O, O) -> O?,
        val priority: Int
) : Operation<O>() {

    init {
        require(textRepresentations.isNotEmpty()) {
            "Operation should specify at least one text representation"
        }
    }

    constructor(textRepresentation: String, inv: (O, O) -> O?, priority: Int) :
            this(setOf(textRepresentation), inv, priority)

    operator fun invoke(leftOperand: O, rightOperand: O) = inv(leftOperand, rightOperand)

    override fun partAsString(): String = textRepresentations.first()

}

internal sealed class UnaryOperation<O>(
        final override val textRepresentations: Set<String>,
        private val inv: (O) -> O?,
        val isTrigonometryOperation: Boolean = false
) : Operation<O>() {

    init {
        require(textRepresentations.isNotEmpty()) {
            "Operation should specify at least one text representation"
        }
    }

    operator fun invoke(operand: O) = inv(operand)

    override fun partAsString(): String = textRepresentations.first()
}

internal class PrefixOperation<O>(
        textRepresentations: Set<String>, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
) : UnaryOperation<O>(textRepresentations, inv, isTrigonometryOperation) {

    constructor(
            textRepresentation: String, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
    ) : this(setOf(textRepresentation), inv, isTrigonometryOperation)

}

internal class PostfixOperation<O>(
        textRepresentations: Set<String>, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
) : UnaryOperation<O>(textRepresentations, inv, isTrigonometryOperation) {

    constructor(
            textRepresentation: String, inv: (O) -> O?, isTrigonometryOperation: Boolean = false
    ) : this(setOf(textRepresentation), inv, isTrigonometryOperation)

}

internal val <O> PrefixOperation<O>.isSignOperation: Boolean
    get() = textRepresentations.contains("+") || textRepresentations.contains("-")