package ru.art2000.extensions.fragments

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

open class NamedReplaceableFragmentCreator<F>(
    @StringRes private val title: Int,
    private val creator: () -> F,
) : INamedReplaceableCreator<F> where F: Fragment, F: IReplaceableFragment {

    override fun getTitle() = title

    override fun createReplaceable() = creator()

}

class NavigationFragmentCreator<F>(
    @DrawableRes private val icon: Int,
    @IdRes private val id: Int,
    @StringRes title: Int,
    creator: () -> F,
) : NamedReplaceableFragmentCreator<F>(title, creator),
    INavigationCreator<F> where F: Fragment, F: IReplaceableFragment {

    override fun getIcon() = icon

    override fun getReplaceableId() = id

}