package ru.art2000.calculator.model.calculator.parts

sealed interface ValueBased<O> : ExpressionPart<O> {

    val value: O

    override fun mayComeAfter(part: ExpressionPart<O>?) =
        part == null || part is PrefixOperation || part is BinaryOperation
}

data class ExpressionValue<O>(override val value: O) : ValueBased<O> {

    override fun toString() = value.toString()

    override fun partAsString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExpressionValue<*>) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}

data class Constant<O>(val symbols: List<String>, override val value: O) : ValueBased<O> {

    init {
        require(symbols.isNotEmpty()) {
            "Constant should specify at least one text representation"
        }
    }

    constructor(symbol: String, value: O) : this(listOf(symbol), value)

    override fun partAsString(): String = symbols.first()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Constant<*>) return false

        if (symbols != other.symbols) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbols.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }


}