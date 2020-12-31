package ru.art2000.calculator.model.calculator

import java.lang.IndexOutOfBoundsException

class StringView internal constructor(
        private val string: CharSequence,
        private val from: Int,
        private val to: Int,
): CharSequence {

    override val length: Int
        get() = to - from

    override fun get(index: Int): Char {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException()

        return string[from + index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        if (startIndex < 0 || endIndex > length || endIndex < startIndex) throw IndexOutOfBoundsException()

        return StringView(string, from + startIndex, from + endIndex)
    }

    override fun toString(): String {
        return string.substring(from, to)
    }
}

fun CharSequence.view(from: Int, to: Int): StringView {
    if (from < 0 || to > length || to < from) throw RuntimeException()

    return StringView(this, from, to)
}