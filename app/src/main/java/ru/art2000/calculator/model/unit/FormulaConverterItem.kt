package ru.art2000.calculator.model.unit

import androidx.annotation.StringRes

class FormulaConverterItem<T>(
        @StringRes override val nameResourceId: Int,
        @StringRes override val shortNameResourceId: Int,
        val fromAbsolute: (T) -> T,
        val toAbsolute: (T) -> T,
        zero: T,
) : UnitConverterItemBase<T>(zero) {

    override fun absoluteToCurrent(absoluteValue: T) = fromAbsolute(absoluteValue)

    override fun currentToAbsolute(currentValue: T) = toAbsolute(currentValue)

    override fun isSameItem(anotherItem: UnitConverterItem<T>): Boolean {
        if (anotherItem !is FormulaConverterItem) {
            return false
        }

        return super.isSameItem(anotherItem)
                && fromAbsolute == anotherItem.fromAbsolute
                && toAbsolute == anotherItem.toAbsolute
    }
}

@Suppress("FunctionName")
fun DoubleFormulaConverterItem(
        @StringRes nameResourceId: Int,
        @StringRes shortNameResourceId: Int,
        fromAbsolute: (Double) -> Double,
        toAbsolute: (Double) -> Double,
) = FormulaConverterItem(nameResourceId, shortNameResourceId, fromAbsolute, toAbsolute, 0.0)