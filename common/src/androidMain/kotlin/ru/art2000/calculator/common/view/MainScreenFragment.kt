package ru.art2000.calculator.common.view

import androidx.annotation.LayoutRes
import ru.art2000.extensions.activities.IEdgeToEdgeFragment
import ru.art2000.extensions.fragments.CommonReplaceableFragment

abstract class MainScreenFragment(
    @LayoutRes contentLayoutId: Int = 0,
) : CommonReplaceableFragment(contentLayoutId), IEdgeToEdgeFragment