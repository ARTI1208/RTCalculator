package ru.art2000.calculator.calculator.computation.parts

internal data class Constant<O>(val symbols: List<String>, val value: O) : ExpressionPart<O> {

    init {
        require(symbols.isNotEmpty()) {
            "Operation should specify at least one text representation"
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
