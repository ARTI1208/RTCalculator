package ru.art2000.extensions

import android.content.Context
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import androidx.appcompat.widget.*


class CalculatorEditText(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
) : AppCompatEditText(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, android.R.attr.editTextStyle)

    constructor(context: Context) : this(context, null)

    public var onSelectionChangedListener: OnSelectionChangedListener? = null

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