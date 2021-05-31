package ru.art2000.extensions.fragments

import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.preference.PreferenceFragmentCompat
import ru.art2000.extensions.preferences.TimePickerPreference
import ru.art2000.extensions.preferences.TimePickerPreferenceDialog

abstract class ExtendedPreferenceFragment : PreferenceFragmentCompat() {

    companion object {
        private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"

        val links : MutableMap<Class<out Preference>, (String?) -> PreferenceDialogFragmentCompat> = mutableMapOf(
                TimePickerPreference::class.java to TimePickerPreferenceDialog::newInstance
        )
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        try {
            super.onDisplayPreferenceDialog(preference)
        } catch (_: IllegalArgumentException) {

            links[preference.javaClass]?.also { dialogConstructor ->
                val dialogFragment = dialogConstructor(preference.key)

                // FIXME. Seems like PreferenceDialogFragmentCompat does not yet support proposed replacement
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.show(parentFragmentManager, DIALOG_FRAGMENT_TAG)
            }
        }
    }
}