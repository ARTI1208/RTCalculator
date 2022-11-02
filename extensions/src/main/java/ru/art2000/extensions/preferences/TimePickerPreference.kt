package ru.art2000.extensions.preferences

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import androidx.preference.DialogPreference
import androidx.preference.Preference
import ru.art2000.extensions.fragments.ExtendedPreferenceFragment
import ru.art2000.extensions.parseStringTime
import androidx.preference.R as PreferenceR


sealed class TimePickerPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = getAttr(
        context,
        PreferenceR.attr.dialogPreferenceStyle,
        android.R.attr.dialogPreferenceStyle
    ),
    defStyleRes: Int = 0
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    var hour: Int = DEFAULT_HOUR
        set(value) {
            field = value
            updateSummary()
        }

    var minute: Int = DEFAULT_MINUTE
        set(value) {
            field = value
            updateSummary()
        }

    private val timeString: String
        get() = String.format("%02d", hour) + ":" + String.format("%02d", minute)

    companion object {
        fun getAttr(context: Context, attr: Int, fallback: Int = attr): Int {
            val value = TypedValue()
            context.theme.resolveAttribute(attr, value, true)
            return if (value.resourceId != 0) {
                attr
            } else fallback
        }

        private const val DEFAULT_VALUE = "00:00"

        private const val DEFAULT_HOUR = 0

        private const val DEFAULT_MINUTE = 0

        fun newPickerDialog(preference: Preference): ExtendedPreferenceFragment.DialogShower {
            val fragment = TimePickerPreferenceDialog()
            val b = Bundle(1)
            b.putString("key", preference.key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    @Deprecated("Deprecated in Java")
    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val value = if (restoreValue) {
            if (defaultValue == null)
                getPersistedString(DEFAULT_VALUE)
            else
                getPersistedString(defaultValue.toString())
        } else {
            defaultValue?.toString()
        } ?: DEFAULT_VALUE

        val timePair = parseStringTime(value)

        hour = timePair.first
        minute = timePair.second
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute

        if (callChangeListener(timeString)) persistString(timeString)
    }

    private fun updateSummary() {
        summary = timeString
    }
}