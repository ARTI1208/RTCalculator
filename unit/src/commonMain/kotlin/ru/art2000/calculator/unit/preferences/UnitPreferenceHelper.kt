package ru.art2000.calculator.unit.preferences

internal interface UnitPreferenceHelper {

    val unitViewType: String

    fun setOnViewTypeChangedListener(onChanged: ((String) -> Unit)?)

}