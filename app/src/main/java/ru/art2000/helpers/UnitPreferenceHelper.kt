package ru.art2000.helpers

interface UnitPreferenceHelper {

    val unitViewType: String

    fun setOnViewTypeChanged(onChanged: ((String) -> Unit)?)

}