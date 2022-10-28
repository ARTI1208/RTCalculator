package ru.art2000.extensions.fragments

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

interface INamedReplaceableCreator<out R : IReplaceableFragment> {

    @StringRes
    fun getTitle(): Int

    fun getTitle(context: Context): CharSequence = context.getString(getTitle())

    fun createReplaceable(): R

}

interface INavigationCreator<out R : IReplaceableFragment> : INamedReplaceableCreator<R> {

    @DrawableRes
    fun getIcon(): Int

    @IdRes
    fun getReplaceableId(): Int

}