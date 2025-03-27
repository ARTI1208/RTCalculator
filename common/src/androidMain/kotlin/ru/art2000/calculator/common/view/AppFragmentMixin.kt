package ru.art2000.calculator.common.view

import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import ru.art2000.extensions.activities.IEdgeToEdgeFragment
import ru.art2000.extensions.views.isDrawingUnderSystemBarsAllowed

interface AppFragmentMixin : IEdgeToEdgeFragment {

    fun Fragment.updateViewOnCreated(createdView: View) {
        applyEdgeToEdgeIfAvailable()
    }

}

@Suppress("UnusedReceiverParameter")
internal fun AppFragmentMixin.onAttachedToWindow(window: Window) {
    check(window.isDrawingUnderSystemBarsAllowed) {
        "MainScreenPages should be added only to activities allowing drawing under status bar"
    }
}