package ru.art2000.extensions.preferences

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Spinner
import androidx.core.view.updateLayoutParams
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceViewHolder
import ru.art2000.helpers.getDimenAttribute

class MyDropDownPreference(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
) : DropDownPreference(context, attrs, defStyleAttr, defStyleRes) {

    private val mSpinner by lazy {
        mSpinnerField.get(this) as Spinner
    }

    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
    ) : this(context, attrs, defStyleAttr, 0)

    constructor(
            context: Context,
            attrs: AttributeSet?,
    ) : this(context, attrs, androidx.preference.R.attr.dropdownPreferenceStyle)

    @Suppress("unused")
    constructor(context: Context) : this(context, null)

    init {
        summaryProvider = SimpleSummaryProvider.getInstance()
    }

    override fun onBindViewHolder(view: PreferenceViewHolder) {
        super.onBindViewHolder(view)

        try {
            mSpinner.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    val paddingStart = context.getDimenAttribute(android.R.attr.listPreferredItemPaddingStart)
                    marginStart = paddingStart
                } else {
                    val paddingLeft = context.getDimenAttribute(android.R.attr.listPreferredItemPaddingLeft)
                    leftMargin = paddingLeft
                }
            }
        } catch (e: Resources.NotFoundException) {
            // TODO context.getDimenAttribute below fails as theme.resolveAttribute returns 0 somewhy
            e.printStackTrace()
        }
    }

    companion object {
        private val mSpinnerField by lazy {
            DropDownPreference::class.java.getDeclaredField("mSpinner")
                    .apply { isAccessible = true }
        }
    }
}