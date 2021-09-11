package ru.art2000.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;

import java.lang.reflect.Field;
import java.util.Objects;

import ru.art2000.calculator.R;

public class SnackbarThemeHelper {

    public static Snackbar createThemedSnackbar(@NonNull View v,
                                                @StringRes int resId,
                                                int duration) {
        return createThemedSnackbar(v, v.getContext().getString(resId), duration);
    }

    @SuppressLint({"PrivateResource", "RestrictedApi"})
    public static Snackbar createThemedSnackbar(@NonNull View v,
                                                @NonNull CharSequence text,
                                                int duration) {
        //Initialization
        Snackbar snackbar = Snackbar.make(v, text, duration);
        ViewGroup snackbarLayout = (ViewGroup) snackbar.getView();
        Context context = snackbar.getContext();
        Resources resources = context.getResources();

        //Dimensions
        int horizontalMargin = resources.getDimensionPixelSize(R.dimen.bottom_view_horizontal_margin);
        float cornerRadius = resources.getDimension(R.dimen.bottom_view_corner_radius);
        int bottomMargin = (int) resources.getDimension(R.dimen.bottom_view_vertical_margin);

        //Parent layout params
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) snackbarLayout.getLayoutParams();
        params.setMargins(horizontalMargin, 0, horizontalMargin, bottomMargin);
        snackbarLayout.setLayoutParams(params);

        fixSnackBarHorizontalMargin(snackbar, horizontalMargin);

        //Content layout params
        SnackbarContentLayout contentLayout =
                (SnackbarContentLayout) snackbarLayout.getChildAt(0);
        Snackbar.SnackbarLayout.LayoutParams lp =
                (Snackbar.SnackbarLayout.LayoutParams) contentLayout.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        contentLayout.setLayoutParams(lp);

        //Background
        MaterialShapeDrawable backgroundDrawable = new MaterialShapeDrawable();
        backgroundDrawable.setStroke(resources.getDimension(R.dimen.bottom_view_stroke_width),
                AndroidHelper.getColorAttribute(context, R.attr.strokeColor));
        backgroundDrawable.setCornerSize(cornerRadius);
        backgroundDrawable.setFillColor(ColorStateList.valueOf(
                AndroidHelper.getColorAttribute(context, R.attr.floatingViewBackground)));
        snackbarLayout.setBackground(backgroundDrawable);

        //MessageView
        snackbar.setTextColor(
                AndroidHelper.getColorAttribute(context, R.attr.colorOnSecondary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentLayout.getMessageView().setLetterSpacing(0);
        }

        //Animation
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);

        return snackbar;
    }

    public static void fixSnackBarHorizontalMargin(Snackbar snackBar, int horizontalMargin) {
        fixSnackBarHorizontalMargin(snackBar, horizontalMargin, horizontalMargin);
    }

    /*
     * TODO remove this when https://github.com/material-components/material-components-android/issues/1076 is fixed
     *
     * This hack is based on https://github.com/material-components/material-components-android/issues/1076#issuecomment-670274203
     */
    public static void fixSnackBarHorizontalMargin(Snackbar snackBar, int leftMargin, int rightMargin) {
        try {
            Class<?> snackbarClass = BaseTransientBottomBar.class;
            Field originalMarginsField = snackbarClass.getDeclaredField("originalMargins");
            originalMarginsField.setAccessible(true);
            Rect fixedOriginalMargins = (Rect) Objects.requireNonNull(originalMarginsField.get(snackBar));
            fixedOriginalMargins.left = leftMargin;
            fixedOriginalMargins.right = rightMargin;
            originalMarginsField.set(snackBar, fixedOriginalMargins);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
