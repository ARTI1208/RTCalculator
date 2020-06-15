package ru.art2000.calculator.unit_converter.model

import androidx.annotation.StringRes
import ru.art2000.calculator.calculator.view_model.CalculationClass

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
        mAbsoluteValue = CalculationClass.calculateDbl(toAbsolute.replace("X", value.toString()))
    }

    override fun convert(from: UnitConverterItem): Double {
        if (from !== this) {
            mAbsoluteValue = from.absoluteValue
            mCurrentValue = CalculationClass.calculateDbl(fromAbsolute.replace("X", from.absoluteValue.toString()))
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