package ru.art2000.calculator.model.unit

import androidx.annotation.StringRes

class RatioConverterItem<T>(
        @StringRes override val nameResourceId: Int,
        @StringRes override val shortNameResourceId: Int,
        val ratio: T,
        val multiply: (T, T) -> T,
        val divide: (T, T) -> T,
        zero: T,
) : UnitConverterItemBase<T>(zero) {

    override fun absoluteToCurrent(absoluteValue: T) = absoluteValue * ratio

    override fun currentToAbsolute(currentValue: T) = currentValue / ratio

    override fun isSameItem(anotherItem: UnitConverterItem<T>): Boolean {
        if (anotherItem !is RatioConverterItem) return false

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