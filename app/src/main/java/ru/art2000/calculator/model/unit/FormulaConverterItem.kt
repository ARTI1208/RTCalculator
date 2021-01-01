package ru.art2000.calculator.model.unit

import androidx.annotation.StringRes
import ru.art2000.calculator.view_model.calculator.CalculationClass

class FormulaConverterItem(@StringRes override val nameResourceId: Int,
                           val fromAbsolute: String,
                           val toAbsolute: String) : UnitConverterItem {

    private var mCurrentValue = 0.0

    private var mAbsoluteValue = 0.0

    override val currentValue: Double
        get() = mCurrentValue

    override val absoluteValue: Double
        get() = mAbsoluteValue

    override fun setValue(value: Double) {
        mCurrentValue = value
        mAbsoluteValue = CalculationClass.calculateForceNumber(toAbsolute.replace("X", value.toString()))
    }

    override fun convert(from: UnitConverterItem): Double {
        if (from !== this) {
            mAbsoluteValue = from.absoluteValue
            mCurrentValue = CalculationClass.calculateForceNumber(fromAbsolute.replace("X", from.absoluteValue.toString()))
        }

        return mCurrentValue
    }

    override fun isSameItem(anotherItem: UnitConverterItem): Boolean {
        if (anotherItem !is FormulaConverterItem) {
            return false
        }

        return super.isSameItem(anotherItem)
                && fromAbsolute == anotherItem.fromAbsolute
                && toAbsolute == anotherItem.toAbsolute
    }
}