package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.computation.numbers.NumberField

sealed class Calculations<T> {

    internal abstract val field: NumberField<T>

    protected abstract val formatter: CalculationNumberFormatter<T>

    fun format(number: T): String = formatter.format(field.toCalculationNumber(number))

    fun calculateForDisplay(expression: String, angleType: AngleType): String  {
        val result = calculate(expression, angleType) ?: return CALCULATION_ERROR

        val calculationNumberResult = field.toCalculationNumber(result)

        if (calculationNumberResult.isInfinite) return CALCULATION_DIVIDE_BY_ZERO

        return formatter.format(calculationNumberResult)
    }

    fun calculateForDisplay(expression: String): String = calculateForDisplay(expression, AngleType.RADIANS)

    abstract fun calculate(expression: String, angleType: AngleType): T?

    fun calculate(expression: String): T? = calculate(expression, AngleType.RADIANS)

    companion object {
        const val CALCULATION_ERROR = "error"

        const val CALCULATION_DIVIDE_BY_ZERO = "zero"
    }
}