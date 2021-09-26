package ru.art2000.calculator.view

import android.view.View
import ru.art2000.extensions.views.applyWindowTopInsets

internal interface MainScreenPage {

    fun updateViewOnCreated(createdView: View) {
        createdView.applyWindowTopInsets()
    }

}