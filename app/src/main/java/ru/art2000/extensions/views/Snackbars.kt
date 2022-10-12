package ru.art2000.extensions.views

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.view.updateLayoutParams
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import ru.art2000.calculator.R
import ru.art2000.helpers.getColorAttribute
import com.google.android.material.R as MaterialR

fun View.createThemedSnackbar(message: CharSequence, duration: Int): Snackbar {

    //Initialization
    val snackbar: Snackbar = Snackbar.make(this, message, duration)
    val snackbarLayout = snackbar.view as ViewGroup
    val context = snackbar.context
    val resources = context.resources

    //Dimensions
    val horizontalMargin =
        resources.getDimensionPixelSize(R.dimen.bottom_view_horizontal_margin)
    val cornerRadius = resources.getDimension(R.dimen.bottom_view_corner_radius)
    val bottomMargin = resources.getDimension(R.dimen.bottom_view_vertical_margin).toInt()

    //Parent layout params
    snackbarLayout.updateLayoutParams<MarginLayoutParams> {
        setMargins(horizontalMargin, 0, horizontalMargin, bottomMargin)
    }

    //Content layout params
    val contentLayout = snackbarLayout.getChildAt(0) as SnackbarContentLayout
    contentLayout.updateLayoutParams<FrameLayout.LayoutParams> {
        gravity = Gravity.CENTER
    }

    //Background
    val backgroundDrawable = MaterialShapeDrawable()
    backgroundDrawable.setStroke(
        resources.getDimension(R.dimen.bottom_view_stroke_width),
        context.getColorAttribute(MaterialR.attr.strokeColor)
    )
    backgroundDrawable.setCornerSize(cornerRadius)
    backgroundDrawable.fillColor = ColorStateList.valueOf(
        context.getColorAttribute(R.attr.floatingViewBackground)
    )
    snackbarLayout.background = backgroundDrawable

    //MessageView
    snackbar.setTextColor(
        context.getColorAttribute(MaterialR.attr.colorOnBackground)
    )

    //Animation
    snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE

    return snackbar
}

fun View.createThemedSnackbar(@StringRes message: Int, duration: Int): Snackbar =
    createThemedSnackbar(context.getString(message), duration)