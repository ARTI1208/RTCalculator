package ru.art2000.extensions.views

import android.content.Context
import android.text.Editable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.annotation.StringRes

fun HorizontalScrollView.postFullScrollRight() {
    postDelayed({ fullScroll(HorizontalScrollView.FOCUS_RIGHT) }, 100L)
}

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
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    emptyView.gravity = Gravity.CENTER
    return emptyView
}