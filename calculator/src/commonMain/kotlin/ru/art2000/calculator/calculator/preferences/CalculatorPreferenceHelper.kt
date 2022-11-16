package ru.art2000.calculator.calculator.preferences

import ru.art2000.calculator.calculator.model.DivideByZero
import ru.art2000.extensions.preferences.PreferenceDelegate

interface CalculatorPreferenceHelper {

    val zeroDivResultProperty: PreferenceDelegate<DivideByZero>

    val zeroDivResult: DivideByZero

    var lastExpression: String?

    var lastExpressionWasCalculated: Boolean

    var lastMemory: String

}