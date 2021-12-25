package ru.art2000.extensions.preferences

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat

class TimePickerPreferenceDialog : PreferenceDialogFragmentCompat() {

    companion object {

        fun newInstance(key: String?): TimePickerPreferenceDialog {
            val fragment = TimePickerPreferenceDialog()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                preference.hour = timePicker.hour
                preference.minute = timePicker.minute
            } else {
                preference.hour = timePicker.currentHour
                preference.minute = timePicker.currentMinute
            }

            val value: String = preference.timeString
            if (preference.callChangeListener(value)) preference.persistString(value)
        }
    }
}