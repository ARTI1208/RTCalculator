package ru.art2000.extensions.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.preference.DialogPreference
import androidx.preference.R as PreferenceR


class TimePickerPreference @JvmOverloads constructor(
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

    val timeString: String
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

        @JvmStatic
        fun parseStringTime(time: String): Pair<Int, Int> {
            val parts = time.split(':')
            if (parts.size != 2) return DEFAULT_HOUR to DEFAULT_MINUTE

            val hour = (parts.first().toIntOrNull() ?: DEFAULT_HOUR)
                .coerceAtLeast(0)
                .coerceAtMost(23)

            val minute = (parts.last().toIntOrNull() ?: DEFAULT_MINUTE)
                .coerceAtLeast(0)
                .coerceAtMost(59)

            return hour to minute
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

    public override fun persistString(value: String?): Boolean {
        return super.persistString(value)
    }

    private fun updateSummary() {
        summary = timeString
    }
}