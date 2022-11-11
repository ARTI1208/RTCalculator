package ru.art2000.extensions.preferences

import android.content.Context
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceDialogFragmentCompat
import ru.art2000.extensions.fragments.ExtendedPreferenceFragment

class TimePickerPreferenceDialog : PreferenceDialogFragmentCompat(),
    ExtendedPreferenceFragment.DialogShower {

    private lateinit var timePicker: TimePicker

    override fun onCreateDialogView(context: Context): View {
        timePicker = TimePicker(context).apply {
            setIs24HourView(true)
        }

        return timePicker
    }

    @Suppress("DEPRECATION")
    override fun onBindDialogView(v: View) {
        super.onBindDialogView(v)
        val preference = preference as TimePickerPreference
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.hour = preference.hour
            timePicker.minute = preference.minute
        } else {
            timePicker.currentHour = preference.hour
            timePicker.currentMinute = preference.minute
        }
    }

    @Suppress("DEPRECATION")
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            val preference = preference as TimePickerPreference

            val hour: Int
            val minute: Int
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hour = timePicker.hour
                minute = timePicker.minute
            } else {
                hour = timePicker.currentHour
                minute = timePicker.currentMinute
            }

            preference.onTimeSelected(hour, minute)
        }
    }

    override fun show(
        preferenceFragment: ExtendedPreferenceFragment,
        fragmentManager: FragmentManager
    ) {
        // FIXME. Seems like PreferenceDialogFragmentCompat does not yet support proposed replacement
        @Suppress("DEPRECATION")
        setTargetFragment(preferenceFragment, 0)
        show(fragmentManager, ExtendedPreferenceFragment.DIALOG_FRAGMENT_TAG)
    }
}