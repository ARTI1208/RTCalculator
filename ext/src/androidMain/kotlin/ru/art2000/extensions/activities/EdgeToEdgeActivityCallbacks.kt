package ru.art2000.extensions.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.res.getColorOrThrow
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

class EdgeToEdgeActivityCallbacks : ActivityCallbacksAdapter {

    @SuppressLint("UseKtx")
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is ComponentActivity) {

            // https://stackoverflow.com/questions/79319740/edge-to-edge-doesnt-work-when-activity-recreated-or-appcompatdelegate-setdefaul
            if (savedInstanceState != null && Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            }

            val navigationBarColor = activity.getNavBarColor()

            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    lightScrim = Color.TRANSPARENT,
                    darkScrim = Color.TRANSPARENT,
                ),
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim = navigationBarColor,
                    darkScrim = navigationBarColor,
                ),
            )
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                EdgeToEdgeFragmentCallbacks, true
            )
        }
    }

    private fun Activity.getNavBarColor() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            Color.TRANSPARENT
        } else {
            @Suppress("DEPRECATION")
            val typedArray = obtainStyledAttributes(
                intArrayOf(android.R.attr.navigationBarColor),
            )
            val navigationBarColor = typedArray.getColorOrThrow(0)
            typedArray.recycle()
            navigationBarColor
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