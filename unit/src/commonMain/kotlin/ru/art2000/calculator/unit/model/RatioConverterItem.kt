package ru.art2000.calculator.unit.model

internal class RatioConverterItem<T>(
        val ratio: T,
        val multiply: (T, T) -> T,
        val divide: (T, T) -> T,
        zero: T,
) : UnitConverterItemBase<T>(zero) {

    override fun absoluteToCurrent(absoluteValue: T) = absoluteValue * ratio

    override fun currentToAbsolute(currentValue: T) = currentValue / ratio

    private operator fun T.times(other: T): T = multiply(this, other)
    private operator fun T.div(other: T): T = divide(this, other)

}