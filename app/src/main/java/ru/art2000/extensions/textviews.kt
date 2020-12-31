package ru.art2000.extensions

import android.text.Editable
import android.widget.EditText
import android.widget.TextView

operator fun TextView.plusAssign(text: CharSequence) {
    append(text)
}

operator fun Editable?.plusAssign(text: CharSequence) {
    this?.append(text)
}

var EditText.textValue: CharSequence
    get() = this.text
    set(value) {
        setText(value)
    }

var TextView.textValue: CharSequence
    get() = this.text
    set(value) {
        text = value
    }