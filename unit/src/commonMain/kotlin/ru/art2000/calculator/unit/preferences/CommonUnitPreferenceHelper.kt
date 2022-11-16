package ru.art2000.calculator.unit.preferences

import ru.art2000.extensions.preferences.AppPreferences
import ru.art2000.extensions.preferences.observe
import ru.art2000.extensions.preferences.stringPreference

internal class CommonUnitPreferenceHelper(preferences: AppPreferences) : UnitPreferenceHelper {

    private val unitViewTypeProperty = preferences.stringPreference(
        UnitKeys.KEY_UNIT_VIEW,
        UnitDefaults.DEFAULT_UNIT_VIEW
    )

    override val unitViewType by unitViewTypeProperty

    private var viewTypeSubscription: (() -> Unit)? = null

    override fun setOnViewTypeChangedListener(onChanged: ((String) -> Unit)?) {
        viewTypeSubscription = if (onChanged != null) {
            unitViewTypeProperty.observe(onChanged)
        } else {
            viewTypeSubscription?.invoke()
            null
        }
    }

}