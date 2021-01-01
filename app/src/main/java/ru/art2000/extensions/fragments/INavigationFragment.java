package ru.art2000.extensions.fragments;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

public interface INavigationFragment extends IUniqueReplaceableFragment {

    @DrawableRes
    int getIcon();

    @IdRes
    int getReplaceableId();

}
