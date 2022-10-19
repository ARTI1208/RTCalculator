package ru.art2000.extensions.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText

class CalculatorEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle,
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var onSelectionChangedListener: OnSelectionChangedListener? = null

    init {
        setShowSoftInputOnFocusCompat(false)
        setBackgroundResource(android.R.color.transparent)
    }

    @Suppress("SameParameterValue")
    fun setShowSoftInputOnFocusCompat(show: Boolean) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            showSoftInputOnFocus = show
        } else {
            val method = EditText::class.java.getMethod(
                    "setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(this, show)
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        onSelectionChangedListener?.onSelectionChanged(selStart, selEnd)
    }

    fun interface OnSelectionChangedListener {

        fun onSelectionChanged(selStart: Int, selEnd: Int)

    }
}