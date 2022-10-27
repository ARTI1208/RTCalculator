package ru.art2000.calculator.view

import android.os.Build
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import ru.art2000.extensions.activities.IEdgeToEdgeFragment
import ru.art2000.extensions.views.isDrawingUnderSystemBarsAllowed

internal interface AppFragmentMixin : IEdgeToEdgeFragment {

    fun Fragment.updateViewOnCreated(createdView: View) {
        applyEdgeToEdgeIfAvailable()
    }

}

@Suppress("unused")
internal fun AppFragmentMixin.onAttachedToWindow(window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        check(window.isDrawingUnderSystemBarsAllowed) {
            "MainScreenPages should be added only to activities allowing drawing under status bar"
        }
    }
}