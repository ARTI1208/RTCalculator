package ru.art2000.calculator.calculator.preferences

internal interface CalculatorPreferenceHelper {

    //    @StringRes
    val zeroDivResult: Int

    var lastExpression: String?

    var lastExpressionWasCalculated: Boolean

    var lastMemory: String

}