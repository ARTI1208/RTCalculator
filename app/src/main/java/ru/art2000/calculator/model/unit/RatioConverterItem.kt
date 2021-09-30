package ru.art2000.calculator.model.unit

import androidx.annotation.StringRes

class RatioConverterItem<T>(
        @StringRes override val nameResourceId: Int,
        @StringRes override val shortNameResourceId: Int,
        val ratio: T,
        val multiply: (T, T) -> T,
        val divide: (T, T) -> T,
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
        mAbsoluteValue = value / ratio
    }

    override fun convert(from: UnitConverterItem<T>): T {
        if (this !== from) {
            mAbsoluteValue = from.absoluteValue
            mCurrentValue = from.absoluteValue * ratio
        }

        return mCurrentValue
    }

    override fun isSameItem(anotherItem: UnitConverterItem<T>): Boolean {
        if (anotherItem !is RatioConverterItem) {
            return false
        }

        return super.isSameItem(anotherItem) && ratio == anotherItem.ratio
    }

    private operator fun T.times(other: T): T = multiply(this, other)
    private operator fun T.div(other: T): T = divide(this, other)

}

@Suppress("FunctionName")
fun DoubleRatioConverterItem(
        @StringRes nameResourceId: Int,
        @StringRes shortNameResourceId: Int,
        ratio: Double,
) = RatioConverterItem(nameResourceId, shortNameResourceId, ratio, Double::times, Double::div, 0.0)