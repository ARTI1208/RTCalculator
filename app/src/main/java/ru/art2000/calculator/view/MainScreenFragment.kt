package ru.art2000.calculator.view

import android.os.Bundle
import android.view.View
import ru.art2000.extensions.fragments.NavigationFragment

internal abstract class MainScreenFragment : NavigationFragment(), MainScreenPage {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViewOnCreated(view)
    }

}