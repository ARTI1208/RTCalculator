package ru.art2000.calculator.model.calculator.numbers

@JvmInline
value class CalculationDouble(override val value: Double) : CalculationNumber<Double> {

    override val field: NumberField<Double>
        get() = DoubleField

    override val isInfinite: Boolean
        get() = value.isInfinite()
}