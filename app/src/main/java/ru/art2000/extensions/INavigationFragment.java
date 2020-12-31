package ru.art2000.extensions;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

public interface INavigationFragment extends IUniqueReplaceableFragment {

    @DrawableRes
    int getIcon();

    @IdRes
    int getReplaceableId();

}
