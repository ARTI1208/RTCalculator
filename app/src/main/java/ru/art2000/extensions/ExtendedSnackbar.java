package ru.art2000.extensions;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import ru.art2000.calculator.Helper;
import ru.art2000.calculator.R;

public class ExtendedSnackbar  {

    public static Snackbar createThemedSnackbar(@NonNull View v, @StringRes int resId, int duration){
        Snackbar snackbar = Snackbar.make(v, resId, duration);
        Context context = snackbar.getContext();
        int horizontalMargin = Helper.dip2px(context, 12.7f);
        int bottomMargin = Helper.dip2px(context, 10f);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(horizontalMargin, 0, horizontalMargin, bottomMargin);
        snackbar.getView().setLayoutParams(params);
        snackbar.setTextColor(Helper.getTextColorSecondary(context));
        snackbar.getView().setBackground(ContextCompat.getDrawable(context, R.drawable.background_snackbar));
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        return snackbar;
    }

}
