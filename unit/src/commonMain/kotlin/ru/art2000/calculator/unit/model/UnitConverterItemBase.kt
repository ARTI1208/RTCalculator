package ru.art2000.calculator.unit.model

internal abstract class UnitConverterItemBase<T>(
    zero: T,
) : UnitConverterItem<T> {

    private var mIsSet: Boolean = false

    private var mCurrentValue = zero
        set(value) {
            mIsSet = true
            field = value
        }

    private var mAbsoluteValue = zero

    override val isSet: Boolean
        get() = mIsSet

    override val currentValue: T
        get() = mCurrentValue

    override val absoluteValue: T
        get() = mAbsoluteValue

    override fun setValue(value: T) {
        mCurrentValue = value
        mAbsoluteValue = currentToAbsolute(value)
    }

    override fun convert(from: UnitConverterItem<T>): T {
        if (this !== from) {
            mAbsoluteValue = from.absoluteValue
            mCurrentValue = absoluteToCurrent(from.absoluteValue)
        }

        return mCurrentValue
    }

    protected abstract fun absoluteToCurrent(absoluteValue: T): T

    protected abstract fun currentToAbsolute(currentValue: T): T

}