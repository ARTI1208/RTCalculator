package ru.art2000.extensions.fragments

import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.art2000.extensions.preferences.MaterialTimePickerPreference
import ru.art2000.extensions.preferences.TimePickerPreference

abstract class ExtendedPreferenceFragment : PreferenceFragmentCompat() {

    interface DialogShower {

        fun show(preferenceFragment: ExtendedPreferenceFragment, fragmentManager: FragmentManager)
    }

    companion object {
        const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"

        val links : MutableMap<Class<out Preference>, (Preference) -> DialogShower> = mutableMapOf(
                TimePickerPreference::class.java to TimePickerPreference::newPickerDialog,
                MaterialTimePickerPreference::class.java to MaterialTimePickerPreference::newPickerDialog,
        )
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        try {
            super.onDisplayPreferenceDialog(preference)
        } catch (_: IllegalArgumentException) {

            links[preference.javaClass]?.also { dialogConstructor ->
                val dialogFragment = dialogConstructor(preference)

                dialogFragment.show(this, parentFragmentManager)
            }
        }
    }
}