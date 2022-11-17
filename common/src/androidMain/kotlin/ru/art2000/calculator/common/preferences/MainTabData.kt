package ru.art2000.calculator.common.preferences

import android.content.Context
import androidx.annotation.StringRes

interface MainTabData {

    fun getTitle(context: Context): CharSequence

    fun getKey(context: Context): CharSequence

}

data class MainTabDataImpl(
    @StringRes private val titleRes: Int,
    private val key: String,
) : MainTabData {

    override fun getTitle(context: Context) = context.getString(titleRes)

    override fun getKey(context: Context) = key

}