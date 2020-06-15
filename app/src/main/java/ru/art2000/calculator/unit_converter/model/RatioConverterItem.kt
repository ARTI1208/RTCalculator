package ru.art2000.calculator.unit_converter.model

import androidx.annotation.StringRes

class RatioConverterItem(@StringRes override val nameResourceId: Int, val ratio: Double) : UnitConverterItem {

    private var mCurrentValue = 0.0

    private var mAbsoluteValue = 0.0

    override val currentValue: Double
        get() = mCurrentValue

    override val absoluteValue: Double
        get() = mAbsoluteValue

    override fun setValue(value: Double) {
        mCurrentValue = value
        mAbsoluteValue = value / ratio
    }

    override fun convert(from: UnitConverterItem): Double {
        if (this !== from) {
            mAbsoluteValue = from.absoluteValue
            mCurrentValue = from.absoluteValue * ratio
        }

        return mCurrentValue
    }

    override fun isSameItem(anotherItem: UnitConverterItem): Boolean {
        if (anotherItem !is RatioConverterItem) {
            return false
        }

        return super.isSameItem(anotherItem) && ratio == anotherItem.ratio
    }
}