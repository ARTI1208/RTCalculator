package ru.art2000.extensions.views

import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import ru.art2000.helpers.SnackbarThemeHelper

fun CoordinatorLayout.createThemedSnackbar(message: CharSequence, duration: Int): Snackbar =
        SnackbarThemeHelper.createThemedSnackbar(this, message, duration)

fun CoordinatorLayout.createThemedSnackbar(@StringRes message: Int, duration: Int): Snackbar =
        SnackbarThemeHelper.createThemedSnackbar(this, message, duration)