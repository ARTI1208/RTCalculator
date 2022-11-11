package ru.art2000.calculator.calculator.computation.numbers

import kotlin.jvm.JvmInline

@JvmInline
internal value class CalculationDouble(override val value: Double) : CalculationNumber<Double> {

    override val isInfinite: Boolean
        get() = value.isInfinite()
}