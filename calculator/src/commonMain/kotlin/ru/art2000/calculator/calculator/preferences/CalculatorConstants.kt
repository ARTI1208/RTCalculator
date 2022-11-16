package ru.art2000.calculator.calculator.preferences

import ru.art2000.calculator.calculator.model.DivideByZero

object CalculatorKeys {
    const val KEY_ZERO_DIVISION = "zero_div"
}

object CalculatorDefaults {
    val DEFAULT_ZERO_DIVISION = DivideByZero.INFINITY
}