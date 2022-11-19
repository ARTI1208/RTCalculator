@file:Suppress("unused")

package ru.art2000.calculator.calculator.computation.numbers

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

internal object CalculatorFormatter : BaseCalculatorFormatter() {

    private val formatter by lazy {
        NSNumberFormatter().apply {
            numberStyle = 1u // decimal
            maximumFractionDigits = this@CalculatorFormatter.maximumFractionDigits.toULong()
            groupingSeparator = ""
        }
    }

    override fun format(number: CalculationNumber<Double>): String {
        return formatter.stringFromNumber(NSNumber(number.value))!!
    }
}