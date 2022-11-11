package ru.art2000.calculator.calculator.computation.parts

internal data class ExpressionValue<O>(val value: O) : ExpressionPart<O> {

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