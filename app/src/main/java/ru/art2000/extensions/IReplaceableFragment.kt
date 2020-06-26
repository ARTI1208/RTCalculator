package ru.art2000.extensions

import androidx.annotation.StringRes

interface IReplaceableFragment : IReplaceable {

    fun getOrder(): Int

    @StringRes
    fun getTitle(): Int

}