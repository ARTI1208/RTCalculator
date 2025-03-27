package ru.art2000.extensions.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Spinner
import androidx.core.view.updateLayoutParams
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceViewHolder
import ru.art2000.extensions.getDimenFromAttribute

class MyDropDownPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = androidx.preference.R.attr.dropdownPreferenceStyle,
        defStyleRes: Int = 0,
) : DropDownPreference(context, attrs, defStyleAttr, defStyleRes) {

    private val mSpinner by lazy {
        mSpinnerField.get(this) as Spinner
    }

    init {
        summaryProvider = SimpleSummaryProvider.getInstance()
    }

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)

        mSpinner.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            val paddingStart = context.getDimenFromAttribute(android.R.attr.listPreferredItemPaddingStart)
            marginStart = paddingStart
        }
    }

    companion object {
        private val mSpinnerField by lazy {
            DropDownPreference::class.java.getDeclaredField("mSpinner")
                    .apply { isAccessible = true }
        }
    }
}