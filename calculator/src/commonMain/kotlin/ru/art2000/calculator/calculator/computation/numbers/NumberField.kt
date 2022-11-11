package ru.art2000.calculator.calculator.computation.numbers

import ru.art2000.calculator.calculator.computation.parts.Constant
import ru.art2000.calculator.calculator.computation.parts.Operation
import ru.art2000.calculator.calculator.computation.parts.PrefixOperation

internal interface NumberField<T> {

    val operations: List<Operation<T>>

    val constants: List<Constant<T>>

    val negateOperation: PrefixOperation<T>

    val identityOperation: PrefixOperation<T>

    fun toCalculationNumber(value: T): CalculationNumber<T>

    fun isNumberPart(c: Char): Boolean

    fun isZeroOrClose(fieldNumber: String): Boolean
}