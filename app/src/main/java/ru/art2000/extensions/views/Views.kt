package ru.art2000.extensions.views

import android.content.Context
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener

operator fun TextView.plusAssign(text: CharSequence) {
    append(text)
}

operator fun Editable?.plusAssign(text: CharSequence) {
    this?.append(text)
}

var TextView.textValue: CharSequence
    get() = this.text
    set(value) {
        text = value
    }

fun createTextEmptyView(context: Context, @StringRes text: Int): TextView {
    val emptyView = TextView(context)
    emptyView.setText(text)
    emptyView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    )
    emptyView.gravity = Gravity.CENTER
    return emptyView
}

fun HorizontalScrollView.autoScrollOnInput() {
    val childEditText = getChildAt(0) as? EditText ?: return

    var textChanged = false
    childEditText.addTextChangedListener {
        textChanged = true
    }

    childEditText.viewTreeObserver.addOnPreDrawListener {
        if (textChanged) {
            textChanged = false
            val layout = childEditText.layout ?: return@addOnPreDrawListener true
            val (first, second) = childEditText.selectionStart to childEditText.selectionEnd
            if (first != second) return@addOnPreDrawListener true
            var xCoordinate = layout.getPrimaryHorizontal(first).toInt()
            val xCoordinate2 = layout.getSecondaryHorizontal(first).toInt()
            xCoordinate =
                if (childEditText.layoutDirection == View.LAYOUT_DIRECTION_LTR) xCoordinate else xCoordinate2
            val scrollView = this

            val totalPadding =
                scrollView.paddingStart + scrollView.paddingEnd + childEditText.paddingStart + childEditText.paddingEnd

            var scrollToX =
                if (xCoordinate > scrollView.width) xCoordinate - scrollView.width + totalPadding else xCoordinate
            var isOutOfScreenToStart = false
            if (first > 0) {
                val previousX = layout.getPrimaryHorizontal(first - 1).toInt()
                isOutOfScreenToStart = previousX - scrollView.scrollX < 0
                if (isOutOfScreenToStart) {
                    scrollToX = scrollToX - xCoordinate + previousX
                }
            }
            val isOutOfScreenToEnd =
                xCoordinate - scrollView.scrollX > scrollView.width - totalPadding
            if (isOutOfScreenToStart || isOutOfScreenToEnd) {
                scrollView.scrollTo(scrollToX, 0)
            }
        }
        true
    }
}