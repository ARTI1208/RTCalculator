package ru.art2000.calculator.model.calculator.numbers

import ru.art2000.calculator.model.calculator.parts.Constant
import ru.art2000.calculator.model.calculator.parts.Operation
import ru.art2000.calculator.model.calculator.parts.PrefixOperation

interface NumberField<T> {

    val operations: List<Operation<T>>

    val constants: List<Constant<T>>

    val negateOperation: PrefixOperation<T>

    val identityOperation: PrefixOperation<T>

    fun toCalculationNumber(value: T): CalculationNumber<T>

    fun isNumberPart(c: Char): Boolean

}