package ru.art2000.helpers

interface UnitPreferenceHelper {

    val unitViewType: String

    fun setOnViewTypeChangedListener(onChanged: ((String) -> Unit)?)

}