package ru.art2000.extensions.fragments

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

interface INavigationFragment : IUniqueReplaceableFragment {

    @DrawableRes
    fun getIcon(): Int

    @IdRes
    fun getReplaceableId(): Int

}