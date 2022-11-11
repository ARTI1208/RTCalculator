package ru.art2000.calculator.calculator.computation.numbers

import ru.art2000.calculator.calculator.computation.parts.BinaryOperation
import ru.art2000.calculator.calculator.computation.parts.Constant
import ru.art2000.calculator.calculator.computation.parts.Operation
import ru.art2000.calculator.calculator.computation.parts.PrefixOperation

internal object IntField : NumberField<Int> {

    override val negateOperation = PrefixOperation<Int>("-", { -it })

    override val identityOperation = PrefixOperation<Int>("+", { it })

    override val operations: List<Operation<Int>> = listOf(
        negateOperation,
        identityOperation,
        BinaryOperation("+", { a, b -> a + b }, 0),
        BinaryOperation("-", { a, b -> a - b }, 0),
    )

    override val constants: List<Constant<Int>>
        get() = emptyList()

    override fun toCalculationNumber(value: Int) = object : CalculationNumber<Int> {

        override val value = value

        override val isInfinite = false

    }

    override fun isNumberPart(c: Char): Boolean {
        return c in '0'..'9'
    }

    override fun isZeroOrClose(fieldNumber: String): Boolean {
        return fieldNumber.toInt() == 0
    }

}