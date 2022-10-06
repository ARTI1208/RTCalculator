package ru.art2000.extensions.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ru.art2000.extensions.fragments.ExtendedPreferenceFragment

class MaterialTimePickerPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = getAttr(
        context,
        R.attr.dialogPreferenceStyle,
        android.R.attr.dialogPreferenceStyle
    ),
    defStyleRes: Int = 0
) : TimePickerPreference(context, attrs, defStyleAttr, defStyleRes) {

    class MaterialTimePickerDialog(
        private val preference: MaterialTimePickerPreference
    ) : ExtendedPreferenceFragment.DialogShower {

        override fun show(
            preferenceFragment: ExtendedPreferenceFragment,
            fragmentManager: FragmentManager
        ) {

            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(preference.hour)
                .setMinute(preference.minute)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTitleText(preference.title)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                preference.onTimeSelected(timePicker.hour, timePicker.minute)
            }

            timePicker.show(fragmentManager, ExtendedPreferenceFragment.DIALOG_FRAGMENT_TAG)
        }

    }

    companion object {

        fun newPickerDialog(preference: Preference): ExtendedPreferenceFragment.DialogShower {
            require(preference is MaterialTimePickerPreference)
            return MaterialTimePickerDialog(preference)
        }
    }

}