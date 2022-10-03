package ru.art2000.calculator.model.calculator.numbers

import ru.art2000.calculator.model.calculator.parts.Constant
import ru.art2000.calculator.model.calculator.parts.Operation
import ru.art2000.calculator.model.calculator.parts.UnaryOperation

interface NumberField<T> {

    val operations: List<Operation<T>>

    val constants: List<Constant<T>>

    val negateOperation: UnaryOperation<T>

    val identityOperation: UnaryOperation<T>

    fun toCalculationNumber(value: T): CalculationNumber<T>

    fun isNumberPart(c: Char): Boolean

}