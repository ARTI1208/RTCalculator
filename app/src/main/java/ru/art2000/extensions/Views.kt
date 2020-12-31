package ru.art2000.extensions

import android.widget.HorizontalScrollView

fun HorizontalScrollView.postFullScrollRight() {
    postDelayed({ fullScroll(HorizontalScrollView.FOCUS_RIGHT) }, 100L)
}