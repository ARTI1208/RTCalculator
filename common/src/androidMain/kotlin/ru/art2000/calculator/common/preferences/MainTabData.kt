package ru.art2000.calculator.common.preferences

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ru.art2000.extensions.fragments.INavigationCreator
import ru.art2000.extensions.fragments.IReplaceableFragment
import ru.art2000.extensions.fragments.NavigationFragmentCreator

interface MainTabData<F> where F: Fragment, F: IReplaceableFragment {

    @get:StringRes
    val titleRes: Int

    @get:IdRes
    val idRes: Int

    val key: CharSequence

    val tabCreator: INavigationCreator<F>

}

class MainTabDataImpl<F>(
    @StringRes override val titleRes: Int,
    override val key: String,
    @IdRes override val idRes: Int,
    @DrawableRes iconRes: Int,
    fragmentConstructor: () -> F,
) : MainTabData<F> where F: Fragment, F: IReplaceableFragment {

    override val tabCreator =
        NavigationFragmentCreator(iconRes, idRes, titleRes, fragmentConstructor)
}