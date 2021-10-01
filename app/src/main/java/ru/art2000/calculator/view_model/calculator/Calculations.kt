package ru.art2000.calculator.view_model.calculator

import android.content.Context
import android.view.View
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.numbers.NumberField

interface Calculations<T> {

    val field: NumberField<T>

    val formatter: CalculationNumberFormatter<T>

    fun format(number: T): String = formatter.format(field.toCalculationNumber(number))

    fun calculateForDisplay(expression: String, angleType: AngleType): String  {
        val result = calculate(expression, angleType) ?: return calculationError

        val calculationNumberResult = field.toCalculationNumber(result)

        if (calculationNumberResult.isInfinite) return calculationDivideByZero

        return formatter.format(calculationNumberResult)
    }

    fun calculateForDisplay(expression: String): String = calculateForDisplay(expression, AngleType.RADIANS)

    fun calculate(expression: String, angleType: AngleType): T?

    fun calculate(expression: String): T? = calculate(expression, AngleType.RADIANS)

    fun createDebugView(
            context: Context,
            expression: String,
            angleType: AngleType,
    ): View

    companion object {
        const val calculationError = "error"

        const val calculationDivideByZero = "zero"
    }
}