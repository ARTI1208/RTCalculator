package ru.art2000.helpers

interface CalculatorPreferenceHelper {

    //    @StringRes
    val zeroDivResult: Int

    var lastExpression: String?

    var lastExpressionWasCalculated: Boolean

    var lastMemory: String

}