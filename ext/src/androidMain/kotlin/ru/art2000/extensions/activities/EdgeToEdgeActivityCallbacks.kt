package ru.art2000.extensions.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ru.art2000.extensions.views.isDrawingUnderSystemBarsAllowed

class EdgeToEdgeActivityCallbacks : ActivityCallbacksAdapter {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is ComponentActivity) {
            activity.enableEdgeToEdge()
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                EdgeToEdgeFragmentCallbacks, true
            )
        }
    }

    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is EdgeToEdgeScreen) {
            activity.applyEdgeToEdgeIfAvailable()
        }
    }

    private object EdgeToEdgeFragmentCallbacks : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            if (f is EdgeToEdgeScreen) {
                f.applyEdgeToEdgeIfAvailable()
            }
        }

    }
}