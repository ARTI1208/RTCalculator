package ru.art2000.extensions.fragments

import android.content.Context
import androidx.annotation.StringRes

interface IUniqueReplaceableFragment : IReplaceableFragment {

    @StringRes
    fun getTitle(): Int

    fun getTitle(context: Context): CharSequence = context.getString(getTitle())
}