package ru.art2000.calculator.model.unit

import androidx.annotation.StringRes
import ru.art2000.calculator.view_model.calculator.CalculationClass

class FormulaConverterItem<T>(
    @StringRes override val nameResourceId: Int,
    val fromAbsolute: (T) -> T,
    val toAbsolute: (T) -> T,
    zero: T,
) : UnitConverterItem<T> {

    private var mCurrentValue = zero

    private var mAbsoluteValue = zero

    override val currentValue: T
        get() = mCurrentValue

    override val absoluteValue: T
        get() = mAbsoluteValue

    override fun setValue(value: T) {
        mCurrentValue = value
        mAbsoluteValue = toAbsolute(value)
    }

    override fun convert(from: UnitConverterItem<T>): T {
        if (from !== this) {
            mAbsoluteValue = from.absoluteValue
            mCurrentValue = fromAbsolute(from.absoluteValue)
        }

        return mCurrentValue
    }

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
    fromAbsolute: (Double) -> Double,
    toAbsolute: (Double) -> Double,
) = FormulaConverterItem(nameResourceId, fromAbsolute, toAbsolute, 0.0)