package ru.art2000.calculator.unit.model

internal class FormulaConverterItem<T>(
        val fromAbsolute: (T) -> T,
        val toAbsolute: (T) -> T,
        zero: T,
) : UnitConverterItemBase<T>(zero) {

    override fun absoluteToCurrent(absoluteValue: T) = fromAbsolute(absoluteValue)

    override fun currentToAbsolute(currentValue: T) = toAbsolute(currentValue)
}