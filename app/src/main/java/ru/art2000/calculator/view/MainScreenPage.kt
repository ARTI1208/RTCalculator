package ru.art2000.calculator.view

import android.os.Build
import android.view.View
import android.view.Window
import ru.art2000.extensions.views.applyWindowTopInsets
import ru.art2000.extensions.views.isDrawingUnderStatusBarAllowed

internal interface MainScreenPage {

    fun updateViewOnCreated(createdView: View) {
        createdView.applyWindowTopInsets()
    }

}

@Suppress("unused")
internal fun MainScreenPage.onAttachedToWindow(window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        check(window.isDrawingUnderStatusBarAllowed) {
            "MainScreenPages should be added only to activities allowing drawing under status bar"
        }
    }
}