package ru.art2000.extensions.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle,
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var onSelectionChangedListener: OnSelectionChangedListener? = null

    init {
        showSoftInputOnFocus = false
        setBackgroundResource(android.R.color.transparent)
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        onSelectionChangedListener?.onSelectionChanged(selStart, selEnd)
    }

    fun interface OnSelectionChangedListener {

        fun onSelectionChanged(selStart: Int, selEnd: Int)

    }
}