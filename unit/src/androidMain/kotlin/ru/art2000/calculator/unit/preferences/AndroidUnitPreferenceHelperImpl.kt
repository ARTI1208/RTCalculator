package ru.art2000.calculator.unit.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.art2000.extensions.preferences.stringPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidUnitPreferenceHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UnitPreferenceHelper {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    // Unit ============================================================

    private val unitViewTypeProperty = preferences.stringPreference(
        UnitKeys.KEY_UNIT_VIEW,
        UnitDefaults.DEFAULT_UNIT_VIEW
    )

    override val unitViewType by unitViewTypeProperty

    override fun setOnViewTypeChangedListener(onChanged: ((String) -> Unit)?) {
        if (onChanged != null) {
            unitViewTypeProperty.listen(onChanged)
        } else {
            unitViewTypeProperty.stopListening()
        }
    }

}